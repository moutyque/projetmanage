package small.app.projetmanage.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*
import small.app.projetmanage.R
import small.app.projetmanage.models.Card

class CardListItemsAdapter(private val context: Context, private var list: ArrayList<Card>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = list[position]
        if (holder is CardViewHolder) {
            holder.itemView.tv_card_name.text = card.name
            holder.itemView.setOnClickListener {
                onClickListener?.onClick(card)
            }
            if (card.color.isNotEmpty()) {
                holder.itemView.view_label_color.visibility = View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(card.color))
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)
}