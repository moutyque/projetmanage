package small.app.projetmanage.adapters

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_member.view.*
import small.app.projetmanage.R
import small.app.projetmanage.models.User
import small.app.projetmanage.utils.Constants


class MembersItemAdapter(
    private val context: Context,
    private val list: List<User>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MembersItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_member, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member = list[position]
        Constants.updatePictureInFragment(
            unwrap(context)!!,
            member.image,
            R.drawable.ic_board_place_holder,
            holder.itemView.iv_member_image
        )
        holder.itemView.tv_member_name.text = member.name
        holder.itemView.tv_member_email.text = member.email

        if (member.isSelected) {
            holder.itemView.iv_selected_member.visibility = View.VISIBLE
        } else {
            holder.itemView.iv_selected_member.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (onClickListener != null) onClickListener!!.onClick(member)
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun unwrap(context: Context): Activity? {
        var context: Context? = context
        while (context !is Activity && context is ContextWrapper) {
            context = context.baseContext
        }
        return context as Activity?
    }

    class MembersItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}