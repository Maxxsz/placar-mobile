package ufc.smd.esqueleto_placar

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateUtils.formatElapsedTime
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.getSystemService
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.util.Stack
import kotlinx.coroutines.*

class PlacarActivity : AppCompatActivity() {
    private lateinit var placarATextView: TextView
    private lateinit var placarBTextView: TextView
    private lateinit var cronometroTop: TextView
    private lateinit var cronometroBot: TextView

    private val placarAStack = Stack<Int>()
    private val placarBStack = Stack<Int>()
    private val desfazerStack = Stack<String>()

    private var tempoInicial: Long = 0
    private var cronometroJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)

        placarATextView = findViewById(R.id.placarA)
        placarBTextView = findViewById(R.id.placarB)

        cronometroTop = findViewById(R.id.tvTimerTop)
        cronometroBot = findViewById(R.id.tvTimerBottom)

        val pontosVidaA = intent.getIntExtra("pontosDeVida", 20)
        val pontosVidaB = intent.getIntExtra("pontosDeVida", 20)
//        val nomeJogadorA = intent.getStringExtra("nomeJogadorA") ?: "Jogador A"
//        val nomeJogadorB = intent.getStringExtra("nomeJogadorB") ?: "Jogador B"

        placarAStack.push(pontosVidaA)
        placarBStack.push(pontosVidaB)

        placarATextView.text = pontosVidaA.toString()
        placarBTextView.text = pontosVidaB.toString()
        iniciarCronometro()
    }

    private fun iniciarCronometro() {
        tempoInicial = System.currentTimeMillis()
        cronometroJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val millis = System.currentTimeMillis() - tempoInicial
                val seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60

                cronometroTop.text = String.format("%02d:%02d", minutes, remainingSeconds)
                cronometroBot.text = String.format("%02d:%02d", minutes, remainingSeconds)

                delay(1000) // Suspend the coroutine for 1 second
            }
        }
    }

    fun pararCronometro() {
        cronometroJob?.cancel()
    }

    fun updatePlacarA(v: View) {
        val currentPlacar = placarAStack.peek()
        val newPlacar = currentPlacar - 1
        placarAStack.push(newPlacar)
        placarATextView.text = newPlacar.toString()
        desfazerStack.push("A")
        checkGameOver(newPlacar, "A", v)
    }

    fun updatePlacarB(v: View) {
        val currentPlacar = placarBStack.peek()
        val newPlacar = currentPlacar - 1
        placarBStack.push(newPlacar)
        placarBTextView.text = newPlacar.toString()
        desfazerStack.push("B")
        checkGameOver(newPlacar, "B", v)
    }

    fun desfazerUltimaAlteracao(v: View) {
        if (desfazerStack.isNotEmpty()) {
            when (desfazerStack.pop()) {
                "A" -> {
                    if (placarAStack.size > 1) {
                        placarAStack.pop()
                        placarATextView.text = placarAStack.peek().toString()
                    }
                }
                "B" -> {
                    if (placarBStack.size > 1) {
                        placarBStack.pop()
                        placarBTextView.text = placarBStack.peek().toString()
                    }
                }
            }
        }
    }

    private fun checkGameOver(placar: Int, vencedor: String, v:View) {
        Log.d("PlacarActivity", "Verificando se o jogo terminou...")
        if (placar <= 0) {
            Log.d("PlacarActivity", "Jogo terminado! Jogador $vencedor vence!")
            vibrar(v)
            val message = "Jogador $vencedor vence!"
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Fim de Jogo")
            builder.setMessage(message)
            builder.setPositiveButton("Reiniciar Jogo") { dialog, _ ->
                dialog.dismiss()
                finishGame("Jogador $vencedor")
                restartGame()

            }
            builder.setNegativeButton("Voltar ao Início") { dialog, _ ->
                dialog.dismiss()
                pararCronometro()
                finishGame("Jogador $vencedor")
                returnToMainActivity()
            }
            builder.setCancelable(true)
            builder.show()
        }
    }

    private fun restartGame() {
        val intent = Intent(this, PlacarActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun finishGame(vencedor: String) {
        val pontosVidaA = placarAStack.peek()
        val pontosVidaB = placarBStack.peek()
        val duracao = System.currentTimeMillis() - tempoInicial
        val nomeJogadorA = intent.getStringExtra("nomeJogadorA") ?: "Jogador A"
        val nomeJogadorB = intent.getStringExtra("nomeJogadorB") ?: "Jogador B"

        saveGameResult(vencedor, pontosVidaA, pontosVidaB, duracao, nomeJogadorA, nomeJogadorB)
    }

    fun saveGameResult(vencedor: String, pontosVidaA: Int, pontosVidaB: Int, duracao: Long, nomeJogadorA: String, nomeJogadorB: String) {
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        val edShared = sp.edit()

        val seconds = (duracao / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val formattedDuration = String.format("%02d:%02d", minutes, remainingSeconds)

        val results = sp.getStringSet("results", mutableSetOf())?.toMutableList() ?: mutableListOf()

        val nomeVencedor = if (vencedor == "A") nomeJogadorA else nomeJogadorB

        val result = "$nomeVencedor venceu! $nomeJogadorA: $pontosVidaA, $nomeJogadorB: $pontosVidaB, Duração: $formattedDuration"

        results.add(result)

        if (results.size > 5) {
            results.removeAt(0)
        }

        edShared.putStringSet("results", results.toSet())
        edShared.putInt("numberMatch", results.size) // Atualizar o número de partidas
        edShared.apply()
    }

    fun vibrar(v: View) {
        val buzzer = this.getSystemService<Vibrator>()
        val pattern = longArrayOf(0, 200, 100, 300)
        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                buzzer.vibrate(pattern, -1)
            }
        }
    }
}
