package ufc.smd.esqueleto_placar

import adapters.CustomAdapter
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PreviousGamesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_games)

        // Trazendo o Recycler View
        val recyclerview = findViewById<RecyclerView>(R.id.rcPreviousGames)

        // Tipo de Layout Manager será Linear
        recyclerview.layoutManager = LinearLayoutManager(this)

        // O ArrayList de Placares
        val data = readPLacarDataSharedPreferences()
        // val date = Calendar.getInstance().time
        // var dateTimeFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
        // val data_hora = dateTimeFormat.format(date)


        //Criando 10 Placares
        //   for (i in 1..10) {
        //     val date = Calendar.getInstance().time
        //   var dateTimeFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
        // val data_hora = dateTimeFormat.format(date)
        //data.add(Placar("Jogo "+i,""+i+"x"+i," O jogo foi 4x4 em "+data_hora+"h",true))
        //}

        // ArrayList enviado ao Adapter
        val adapter = CustomAdapter(data)

        // Setando o Adapter no Recyclerview
        recyclerview.adapter = adapter

    }

    fun readPLacarDataSharedPreferences(): ArrayList<Placar> {
        Log.v("PDM", "Lendo o Shared Preferences")
        val data = ArrayList<Placar>()
        val sharedFileName = "PreviousGames"
        var aux: String
        var sp: SharedPreferences = getSharedPreferences(sharedFileName, Context.MODE_PRIVATE)
        if (sp != null) {
            var numMatches = sp.getInt("numberMatch", 0)
            Log.v("PDM", "numMatchs:" + numMatches)
            for (i in 1..numMatches) {
                aux = sp.getString("match" + i, "vazio")!!
                if (!aux.equals("vazio")) {

                    var bis: ByteArrayInputStream
                    bis = ByteArrayInputStream(aux.toByteArray(Charsets.ISO_8859_1))
                    var obi: ObjectInputStream
                    obi = ObjectInputStream(bis)

                    var placar: Placar = obi.readObject() as Placar
                    data.add(placar)
                    //Log.v("PDM", "match"+i+" :"+aux)
                    Log.v("PDM", "Placar: " + placar.nome_partida + " Res:" + placar.resultadoLongo)
                }
            }
        }
        return data
    }

    fun readPLacarData() {
        Log.v("PDM", "Lendo o Shared Preferences")
        val sharedFileName = "PreviousGames"
        var aux: String
        var sp: SharedPreferences = getSharedPreferences(sharedFileName, Context.MODE_PRIVATE)
        if (sp != null) {
            var numMatches = sp.getInt("numberMatch", 0)
            Log.v("PDM", "numMatchs:" + numMatches)
            for (i in 1..numMatches) {
                aux = sp.getString("match" + i, "vazio")!!
                if (!aux.equals("vazio")) {

                    var bis: ByteArrayInputStream
                    bis = ByteArrayInputStream(aux.toByteArray(Charsets.ISO_8859_1))
                    var obi: ObjectInputStream
                    obi = ObjectInputStream(bis)

                    var placar: Placar = obi.readObject() as Placar

                    //Log.v("PDM", "match"+i+" :"+aux)
                    Log.v("PDM", "Placar: " + placar.nome_partida + " Res:" + placar.resultadoLongo)
                }
            }
        }

    }
}
