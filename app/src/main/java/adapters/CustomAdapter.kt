package adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import data.GameResult
import ufc.smd.esqueleto_placar.R

class CustomAdapter(private val context: Context, private val mList: List<GameResult>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    // Criação de Novos ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // infla o card_previous_games
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_previous_game, parent, false)

        return ViewHolder(view)
    }

    // Ligando o Recycler view a um View Holder
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvResultadoJogo: TextView = itemView.findViewById(R.id.tvResultadoJogo)
        val tvGameDate: TextView = itemView.findViewById(R.id.tvGameDate)
        val tvHeart: TextView = itemView.findViewById(R.id.tvHeartCount)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
    }

    // faz o bind de uma ViewHolder a um Objeto da Lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gameResult = mList[position]

        holder.tvResultadoJogo.text = gameResult.result
        holder.tvGameDate.text = gameResult.dataHora
        holder.tvHeart.text = gameResult.heartCount

        if (gameResult.latitude != null && gameResult.longitude != null) {
            holder.tvAddress.visibility = View.VISIBLE

            holder.tvAddress.setOnClickListener {
                val uri = "geo:${gameResult.latitude},${gameResult.longitude}?q=${gameResult.latitude},${gameResult.longitude}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                context.startActivity(intent)
            }
        } else {
            holder.tvAddress.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


}
