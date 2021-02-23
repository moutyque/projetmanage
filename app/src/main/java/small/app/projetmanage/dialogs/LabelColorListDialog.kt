package small.app.projetmanage.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_list.view.*
import small.app.projetmanage.R
import small.app.projetmanage.adapters.LabelColorListItemsAdapter

class LabelColorListDialog(
    context: Context,
    private val title: String,
    private var list: List<String>,
    private val mSelectedColor: MutableLiveData<String>
) : Dialog(context) {
    private var adapter: LabelColorListItemsAdapter? = null

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
        adapter = LabelColorListItemsAdapter(context, list, mSelectedColor)
        view.rvList.adapter = adapter
    }


}