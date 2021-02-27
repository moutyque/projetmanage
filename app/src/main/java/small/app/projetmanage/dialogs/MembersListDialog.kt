package small.app.projetmanage.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_list.view.*
import small.app.projetmanage.R
import small.app.projetmanage.adapters.MembersItemAdapter
import small.app.projetmanage.adapters.OnClickListener
import small.app.projetmanage.models.User

class MembersListDialog(
    context: Context,
    private val title: String,
    private var list: List<User>,
    private var mSelectedUser: MutableLiveData<User>
) : Dialog(context) {
    private var adapter: MembersItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)

    }

    private fun setUpRecyclerView(view: View) {
        view.tvTitle.text = title
        view.rvList.layoutManager = LinearLayoutManager(context)
        adapter = MembersItemAdapter(context, list)
        adapter!!.setOnClickListener(object : OnClickListener {
            override fun onClick(model: Parcelable) {
                var selectedMember = (model as User)
                selectedMember.isSelected = !selectedMember.isSelected
                mSelectedUser.value = selectedMember
            }
        })
        view.rvList.adapter = adapter
    }


}