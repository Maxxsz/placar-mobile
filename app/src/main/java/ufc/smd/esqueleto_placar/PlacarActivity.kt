package ufc.smd.esqueleto_placar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

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

class PlacarActivity : AppCompatActivity() {
    lateinit var placar:Placar
    lateinit var tvResultadoJogo: TextView
    var game =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)
        placar= getIntent().getExtras()?.getSerializable("placar") as Placar
        tvResultadoJogo= findViewById(R.id.tvPlacar)
        //Mudar o nome da partida
        val tvNomePartida=findViewById(R.id.tvNomePartida2) as TextView
        tvNomePartida.text=placar.nome_partida
        ultimoJogos()
    }

    fun alteraPlacar (v:View){
        game++
        if ((game % 2) != 0) {
            placar.resultado = ""+game+" vs "+ (game-1)
        }else{
            placar.resultado = ""+(game-1)+" vs "+ (game-1)
            vibrar(v)
        }
        tvResultadoJogo.text=placar.resultado
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