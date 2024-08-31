package ufc.smd.esqueleto_placar

import adapters.CustomAdapter
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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

        // Trazendo o Recycler View
        val recyclerview = findViewById<RecyclerView>(R.id.rcPreviousGames)

        // Tipo de Layout Manager será Linear
        recyclerview.layoutManager = LinearLayoutManager(this)

        val data = readGameResults()
        // ArrayList enviado ao Adapter
        val adapter = CustomAdapter(data)

        // Setando o Adapter no Recyclerview
        recyclerview.adapter = adapter

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
