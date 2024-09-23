package ufc.smd.esqueleto_placar

import adapters.CustomAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.GameResult

class PreviousGamesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_games)
        val btnAllLocations: Button = findViewById(R.id.btnAllLocations)

        // Trazendo o Recycler View
        val recyclerview = findViewById<RecyclerView>(R.id.rcPreviousGames)

        // Tipo de Layout Manager será Linear
        recyclerview.layoutManager = LinearLayoutManager(this)

        val data = readGameResults()
        // ArrayList enviado ao Adapter
        val adapter = CustomAdapter(this, data)

        // Setando o Adapter no Recyclerview
        recyclerview.adapter = adapter

        btnAllLocations.setOnClickListener {
            val gameResults = readGameResults()
            val uriBuilder = StringBuilder("geo:0,0?q=")

            for (gameResult in gameResults) {
                if (gameResult.latitude != null && gameResult.longitude != null) {
                    uriBuilder.append("${gameResult.latitude},${gameResult.longitude}(Jogo) ")
                }
            }

            if (uriBuilder.toString().contains("q=")) {
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriBuilder.toString()))
                startActivity(mapIntent)
            }
        }

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
}
