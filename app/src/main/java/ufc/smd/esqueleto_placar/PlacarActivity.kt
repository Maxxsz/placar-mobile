package ufc.smd.esqueleto_placar

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.GameResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Stack

class PlacarActivity : AppCompatActivity() {
    private lateinit var placarATextView: TextView
    private lateinit var placarBTextView: TextView
    private lateinit var playerATextView: TextView
    private lateinit var playerBTextView: TextView
    private lateinit var cronometroTop: TextView
    private lateinit var cronometroBot: TextView
    private var gameOver: Boolean = false

    private val placarAStack = Stack<Int>()
    private val placarBStack = Stack<Int>()
    private val desfazerStack = Stack<String>()

    private var tempoInicial: Long = 0
    private var tempoPausado: Long = 0
    private var cronometroJob: Job? = null
    private var cronometroPausado: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)

        placarATextView = findViewById(R.id.placarA)
        placarBTextView = findViewById(R.id.placarB)

        playerATextView = findViewById(R.id.playerA)
        playerBTextView = findViewById(R.id.playerB)

        cronometroTop = findViewById(R.id.tvTimerTop)
        cronometroBot = findViewById(R.id.tvTimerBottom)

        val pontosVidaA = intent.getIntExtra("pontosDeVida", 20)
        val pontosVidaB = intent.getIntExtra("pontosDeVida", 20)
        val nomeJogadorA = intent.getStringExtra("nomeJogadorA") ?: "Player 1"
        val nomeJogadorB = intent.getStringExtra("nomeJogadorB") ?: "Player 2"

        playerATextView.text = nomeJogadorA
        playerBTextView.text = nomeJogadorB

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
                val millis = System.currentTimeMillis() - tempoInicial + tempoPausado
                val seconds = (millis / 1000).toInt()
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60

                cronometroTop.text = String.format("%02d:%02d", minutes, remainingSeconds)
                cronometroBot.text = String.format("%02d:%02d", minutes, remainingSeconds)

                delay(1000)
            }
        }
        cronometroPausado = false
    }

    fun pararCronometro() {
        if (!cronometroPausado) {
            cronometroJob?.cancel()
            tempoPausado += System.currentTimeMillis() - tempoInicial
            cronometroPausado = true
        }
    }

    fun continuarCronometro() {
        if (cronometroPausado) {
            tempoInicial = System.currentTimeMillis()
            cronometroJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    val millis = System.currentTimeMillis() - tempoInicial + tempoPausado
                    val seconds = (millis / 1000).toInt()
                    val minutes = seconds / 60
                    val remainingSeconds = seconds % 60

                    cronometroTop.text = String.format("%02d:%02d", minutes, remainingSeconds)
                    cronometroBot.text = String.format("%02d:%02d", minutes, remainingSeconds)

                    delay(1000)
                }
            }
            cronometroPausado = false
        }
    }

    fun updatePlacarA(v: View) {
        if (gameOver) return

        val nomeJogador = intent.getStringExtra("nomeJogadorA") ?: "Player 1"
        val currentPlacar = placarAStack.peek()
        val newPlacar = currentPlacar - 1

        if (newPlacar >= 0) {
            placarAStack.push(newPlacar)
            placarATextView.text = newPlacar.toString()
            desfazerStack.push("A")
            checkGameOver(newPlacar, nomeJogador, v)
        }
    }

    fun updatePlacarB(v: View) {
        if (gameOver) return

        val nomeJogador = intent.getStringExtra("nomeJogadorB") ?: "Player 2"
        val currentPlacar = placarBStack.peek()
        val newPlacar = currentPlacar - 1

        if (newPlacar >= 0) {
            placarBStack.push(newPlacar)
            placarBTextView.text = newPlacar.toString()
            desfazerStack.push("B")
            checkGameOver(newPlacar, nomeJogador, v)
        }
    }

    fun desfazerUltimaAlteracao(v: View) {
        if (desfazerStack.isNotEmpty()) {
            gameOver = false
            continuarCronometro()
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
        if (placar <= 0) {
            gameOver = true
            vibrar(v)
            val message = "Jogador $vencedor vence!"
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Fim de Jogo")
            builder.setMessage(message)
            pararCronometro()
            builder.setPositiveButton("Reiniciar Jogo") { dialog, _ ->
                dialog.dismiss()
                finishGame(vencedor)
                restartGame(v)
            }
            builder.setNegativeButton("Voltar ao Início") { dialog, _ ->
                dialog.dismiss()
                pararCronometro()
                finishGame(vencedor)
                returnToMainActivity(v)
            }
            builder.setCancelable(true)
            builder.show()
        }
    }

    fun restartGame(v: View) {
        if (!gameOver) {
            showConfirmationDialog(
                "Confirmação",
                "Tem certeza de que deseja reiniciar o jogo? O progresso atual será perdido.",
                onConfirm = {
                    val nomeJogadorA = intent.getStringExtra("nomeJogadorA") ?: "Player 1"
                    val nomeJogadorB = intent.getStringExtra("nomeJogadorB") ?: "Player 2"
                    val pontosVida = intent.getIntExtra("pontosDeVida", 20)

                    val intent = Intent(this, PlacarActivity::class.java)
                    intent.putExtra("nomeJogadorA", nomeJogadorA)
                    intent.putExtra("nomeJogadorB", nomeJogadorB)
                    intent.putExtra("pontosDeVida", pontosVida)

                    startActivity(intent)
                    finish()
                }
            )
        } else {
            val nomeJogadorA = intent.getStringExtra("nomeJogadorA") ?: "Player 1"
            val nomeJogadorB = intent.getStringExtra("nomeJogadorB") ?: "Player 2"
            val pontosVida = intent.getIntExtra("pontosDeVida", 20)

            val intent = Intent(this, PlacarActivity::class.java)
            intent.putExtra("nomeJogadorA", nomeJogadorA)
            intent.putExtra("nomeJogadorB", nomeJogadorB)
            intent.putExtra("pontosDeVida", pontosVida)

            startActivity(intent)
            finish()
        }
    }

    fun returnToMainActivity(v: View) {
        if (!gameOver) {
            showConfirmationDialog(
                "Confirmação",
                "Tem certeza de que deseja voltar ao início? O progresso atual será perdido.",
                onConfirm = {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Sim") { dialog, _ ->
            onConfirm()
            dialog.dismiss()
        }
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(true)
        builder.show()
    }

    private fun readGameResults(): List<GameResult> {
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)

        val gson = Gson()
        val json = sp.getString("game_results", null)

        if (json != null) {
            val type = object : TypeToken<List<GameResult>>() {}.type
            return gson.fromJson(json, type)
        }

        return emptyList()
    }

    fun addGameResult(newResult: GameResult) {
        val currentResults = readGameResults().toMutableList()

        currentResults.add(0, newResult)

        if (currentResults.size > 5) {
            currentResults.removeAt(currentResults.size - 1)
        }

        saveGameResults(currentResults)
    }

    fun finishGame(vencedor: String) {
        val pontosVida = intent.getIntExtra("pontosDeVida", 20)
        val duracao = System.currentTimeMillis() - tempoInicial
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        val dataHora = dateFormat.format(Date())

        val seconds = (duracao / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        val formattedDuration = String.format("%02d:%02d", minutes, remainingSeconds)


        val resultText = "$vencedor venceu! Duração: $formattedDuration"
        val gameResult = GameResult(resultText, formattedDuration, pontosVida.toString(), dataHora)

        addGameResult(gameResult)
    }

    fun saveGameResults(results: List<GameResult>) {
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        val edShared = sp.edit()

        val gson = Gson()
        val json = gson.toJson(results)
        edShared.putString("game_results", json)
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
