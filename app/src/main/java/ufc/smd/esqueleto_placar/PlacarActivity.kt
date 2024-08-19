package ufc.smd.esqueleto_placar

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateUtils.formatElapsedTime

import android.util.Log
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import androidx.core.content.getSystemService
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.util.Stack

class PlacarActivity : AppCompatActivity() {
    lateinit var placar:Placar
    private lateinit var placarATextView: TextView
    private lateinit var placarBTextView: TextView

    private val placarAStack = Stack<Int>()
    private val placarBStack = Stack<Int>()
    private val desfazerStack = Stack<String>()
//    private lateinit var chronometer: Chronometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)

        placarATextView = findViewById(R.id.placarA)
        placarBTextView = findViewById(R.id.placarB)
//        chronometer = findViewById(R.id.chronometer)

        placarAStack.push(20)
        placarBStack.push(20)

//        chronometer.base = SystemClock.elapsedRealtime()
//        chronometer.start()
        ultimoJogos()
    }

    fun updatePlacarA(view: View) {
        val currentPlacar = placarAStack.peek()
        val newPlacar = currentPlacar - 1
        placarAStack.push(newPlacar)
        placarATextView.text = newPlacar.toString()
        desfazerStack.push("A")
        checkGameOver(newPlacar, "A")
    }

    fun updatePlacarB(view: View) {
        val currentPlacar = placarBStack.peek()
        val newPlacar = currentPlacar - 1
        placarBStack.push(newPlacar)
        placarBTextView.text = newPlacar.toString()
        desfazerStack.push("B")
        checkGameOver(newPlacar, "B")

    }

    fun desfazerUltimaAlteracao(view: View) {
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

    private fun checkGameOver(placar: Int, vencedor: String) {
        Log.d("PlacarActivity", "Verificando se o jogo terminou...")
        if (placar <= 0) {
            Log.d("PlacarActivity", "Jogo terminado! Jogador $vencedor vence!")
//            chronometer.stop()
//            val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
//            saveGameDuration(elapsedMillis)
            //  Tempo de jogo: ${formatElapsedTime(elapsedMillis)}
            val message = "Jogador $vencedor vence!"
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Fim de Jogo")
            builder.setMessage(message)
            builder.setPositiveButton("Reiniciar Jogo") { dialog, _ ->
                dialog.dismiss()
                restartGame()
            }
            builder.setNegativeButton("Voltar ao Início") { dialog, _ ->
                dialog.dismiss()
                returnToMainActivity()
            }
            builder.setCancelable(true)
            builder.show()
        }
    }

    private fun restartGame() {
        placarAStack.clear()
        placarBStack.clear()
        placarAStack.push(20)
        placarBStack.push(20)
        placarATextView.text = placarAStack.peek().toString()
        placarBTextView.text = placarBStack.peek().toString()
//        chronometer.base = SystemClock.elapsedRealtime()
//        chronometer.start()
    }

    private fun returnToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveGameDuration(duracao: Long) {
        Log.d("PlacarActivity", "Duração do jogo: ${formatElapsedTime(duracao)}")
    }




    fun vibrar (v:View){
        val buzzer = this.getSystemService<Vibrator>()
         val pattern = longArrayOf(0, 200, 100, 300)
         buzzer?.let {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
             } else {
                 //deprecated in API 26
                 buzzer.vibrate(pattern, -1)
             }
         }

    }


    fun saveGame(v: View) {

        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        var edShared = sp.edit()
        //Salvar o número de jogos já armazenados
        var numMatches= sp.getInt("numberMatch",0) + 1
        edShared.putInt("numberMatch", numMatches)

        //Escrita em Bytes de Um objeto Serializável
        var dt= ByteArrayOutputStream()
        var oos = ObjectOutputStream(dt);
        oos.writeObject(placar);

        //Salvar como "match"
        edShared.putString("match"+numMatches, dt.toString(StandardCharsets.ISO_8859_1.name()))
        edShared.commit() //Não esqueçam de comitar!!!

    }

    fun lerUltimosJogos(v: View){
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)

        var meuObjString:String= sp.getString("match1","").toString()
        if (meuObjString.length >=1) {
            var dis = ByteArrayInputStream(meuObjString.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var placarAntigo:Placar=oos.readObject() as Placar
            Log.v("SMD26",placar.resultado)
        }
    }




    fun ultimoJogos () {
        val sharedFilename = "PreviousGames"
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        var matchStr:String=sp.getString("match1","").toString()
       // Log.v("PDM22", matchStr)
        if (matchStr.length >=1){
            var dis = ByteArrayInputStream(matchStr.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var prevPlacar:Placar = oos.readObject() as Placar
            Log.v("PDM22", "Jogo Salvo:"+ prevPlacar.resultado)
        }

    }
}