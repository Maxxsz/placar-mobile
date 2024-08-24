package adapters

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import data.Placar
import ufc.smd.esqueleto_placar.R

class CustomAdapter(private val mList: List<String>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    // Criação de Novos ViewHolders
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // infla o card_previous_games
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_previous_game, parent, false)

        return ViewHolder(view)
    }

    // Ligando o Recycler view a um View Holder
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvResultadoJogo: TextView = itemView.findViewById(R.id.tvResultadoJogo)
        val lnCell: LinearLayout = itemView.findViewById(R.id.lnCell)
    }

    // faz o bind de uma ViewHolder a um Objeto da Lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val resultado = mList[position]

        // Alimentando os elementos a partir do resultado
        holder.tvResultadoJogo.text = resultado

        holder.lnCell.setOnClickListener {
            val snack = Snackbar.make(holder.lnCell, resultado, Snackbar.LENGTH_LONG)
            snack.show()
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }


}
