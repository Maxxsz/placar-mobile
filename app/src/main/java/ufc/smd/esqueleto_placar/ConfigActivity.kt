package ufc.smd.esqueleto_placar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        openConfig()
        initInterface()
    }

    private fun saveConfig() {
        val sharedFilename = "configPlacar"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        val edShared = sp.edit()

        val pontosDeVida = findViewById<EditText>(R.id.editPV).text.toString().toIntOrNull() ?: 20
        val nomeJogadorA = findViewById<EditText>(R.id.editTextNomeA).text.toString()
        val nomeJogadorB = findViewById<EditText>(R.id.editTextNomeB).text.toString()

        edShared.putInt("pontosDeVida", pontosDeVida)
        edShared.putString("nomeJogadorA", nomeJogadorA)
        edShared.putString("nomeJogadorB", nomeJogadorB)
        edShared.apply()
    }

    private fun openConfig() {
        val sharedFilename = "configPlacar"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)

        val pontosDeVida = sp.getInt("pontosDeVida", 20)

        findViewById<EditText>(R.id.editPV).setText(pontosDeVida.toString())
    }

    fun openPlacar(v: View) {
        saveConfig()
        val nomeJogadorA = findViewById<EditText>(R.id.editTextNomeA).text.toString().takeIf { it.isNotEmpty() }
        val nomeJogadorB = findViewById<EditText>(R.id.editTextNomeB).text.toString().takeIf { it.isNotEmpty() }


        val intent = Intent(this, PlacarActivity::class.java).apply {
            putExtra("pontosDeVida", findViewById<EditText>(R.id.editPV).text.toString().toIntOrNull() ?: 20)
            putExtra("nomeJogadorA", nomeJogadorA)
            putExtra("nomeJogadorB", nomeJogadorB)
        }
        startActivity(intent)
    }

    private fun initInterface() {
        val tv = findViewById<EditText>(R.id.editPV)
    }
}
