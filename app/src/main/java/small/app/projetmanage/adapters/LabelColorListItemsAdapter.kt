package small.app.projetmanage.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_label_color.view.*
import small.app.projetmanage.R

class LabelColorListItemsAdapter(
    private val context: Context,
    private var list: List<String>,
    private val mSelectedColor: MutableLiveData<String>//TODO pas sur s'il faut prendre une ld ou un string directe
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LabelColorListItemsViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_label_color, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is LabelColorListItemsViewHolder) {
            holder.itemView.view_main.setBackgroundColor(Color.parseColor(item))
            if (item == mSelectedColor.value) {
                holder.itemView.iv_selected_color.visibility = View.VISIBLE
            } else {
                holder.itemView.iv_selected_color.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                mSelectedColor.value = item
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class LabelColorListItemsViewHolder(view: View) : RecyclerView.ViewHolder(view)

}