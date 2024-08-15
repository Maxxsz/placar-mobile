package ufc.smd.esqueleto_placar

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import data.UIEducacionalPermissao

class PermissionActivity : AppCompatActivity(), UIEducacionalPermissao.NoticeDialogListener {

    lateinit var requestPermissionLauncher:androidx.activity.result.ActivityResultLauncher<String>
    lateinit var requestLocalPermissionLauncher:androidx.activity.result.ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermissionLauncher= registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    ligarFunc("tel:+5588999999999")
                } else {
                    Snackbar.make(
                        findViewById(R.id.layoutPermission),
                        R.string.semPerLigar,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        requestLocalPermissionLauncher= registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                //Localização
                Log.v("PDM","Acesso à localização concedido")
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        Log.v("PDM",location.toString())
                    }
            } else {
                Snackbar.make(
                    findViewById(R.id.layoutPermission),
                    R.string.semPerLoca,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun ligarWhen(v:View){
        when{
            //Primeiro Caso do When - A permissão já foi concedida
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED ->{
                ligarFunc("tel:+558588888889")
            }
            //Permissão foi negada, mas não para sempre
            shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) ->{
                // Chamar a UI Educacional
                val mensagem =
                    "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada"
                val titulo = "Permissão de acesso a chamadas"
                val codigo = 1 //Código da requisição
                val mensagemPermissao = UIEducacionalPermissao(mensagem, titulo, codigo)
                mensagemPermissao.onAttach(this as Context)
                mensagemPermissao.show(supportFragmentManager, "primeiravez")
            }
            // Permissão negada ou não foi pedida
            else ->{
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    fun sendLocationWhatsApp(latitude: String, longitude:String) {
        val whatsAppMessage = "http://maps.google.com/maps?saddr=$latitude,$longitude"
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppMessage)
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.whatsapp")
        startActivity(sendIntent)
    }
    fun getLocal(v:View){
        when{
            //Primeiro Caso do When - A permissão já foi concedida
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ->{
                Log.v("PDM","Tem permissão de localização")
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        if(location!=null){
                            Log.v("PDM","Lat:"+location?.latitude)
                            sendLocationWhatsApp(location?.latitude.toString(), location?.longitude.toString())
                        }

                    }
            }
            //Permissão foi negada, mas não para sempre
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) ->{
                // Chamar a UI Educacional
                val mensagem =
                    "Nossa aplicação precisa acessar  a localização"
                val titulo = "Permissão de acesso a localização"
                val codigo = 2//Código da requisição
                val mensagemPermissao = UIEducacionalPermissao(mensagem, titulo, codigo)
                mensagemPermissao.onAttach(this as Context)
                mensagemPermissao.show(supportFragmentManager, "primeiravez")
            }
            // Permissão negada ou não foi pedida
            else ->{
                requestLocalPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    fun ligarFunc (numeroCall:String){
        val uri = Uri.parse(numeroCall)
        val itLigar = Intent(Intent.ACTION_CALL, uri)
        startActivity(itLigar)
    }

    override fun onDialogPositiveClick(codigo: Int) {

        //Método chamado pela caixa de diálogo
        Log.v("PDM","Apertou OK")
        if(codigo==1){
        requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
        if(codigo==2){
            requestLocalPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}