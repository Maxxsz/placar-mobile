package adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import data.GameResult
import ufc.smd.esqueleto_placar.R

class CustomAdapter(private val mList: List<GameResult>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


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
        val lnCell: LinearLayout = itemView.findViewById(R.id.lnCell)
    }

    // faz o bind de uma ViewHolder a um Objeto da Lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gameResult = mList[position]

        holder.tvResultadoJogo.text = gameResult.result
        holder.tvGameDate.text = gameResult.dataHora
        holder.tvHeart.text = gameResult.heartCount

        holder.lnCell.setOnClickListener {
            val snack = Snackbar.make(holder.lnCell, "${gameResult.result} - ${gameResult.heartCount}", Snackbar.LENGTH_LONG)
            snack.show()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}
