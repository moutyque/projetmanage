package small.app.projetmanage.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_member.view.*
import small.app.projetmanage.R
import small.app.projetmanage.models.User
import small.app.projetmanage.utils.Constants

class MembersItemAdapter(private val context: Context, private val list: List<User>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MembersItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_member, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val member = list[position]
        Constants.updatePictureInFragment(
            context as Activity,
            member.image,
            R.drawable.ic_board_place_holder,
            holder.itemView.iv_member_image
        )
        holder.itemView.tv_member_name.text = member.name
        holder.itemView.tv_member_email.text = member.email


    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MembersItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}