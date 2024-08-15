package ufc.smd.esqueleto_placar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import data.Placar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun openConfig(v: View){
        val intent = Intent(this, ConfigActivity::class.java).apply{}
        startActivity(intent)

    }

    fun openPermission(v: View){
        val intent = Intent(this, PermissionActivity::class.java).apply{}
        startActivity(intent)
    }
    fun openPreviusGames(v: View) {
        val intent = Intent(this, PreviousGamesActivity::class.java).apply {

        }
        startActivity(intent)

    }
}