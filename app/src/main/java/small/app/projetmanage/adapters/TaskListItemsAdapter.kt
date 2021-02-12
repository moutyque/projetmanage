package small.app.projetmanage.adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tasks.view.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.fragments.TaskListFragment
import small.app.projetmanage.models.Board
import small.app.projetmanage.models.Task


open class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>,
    val board: Board,
    val frag: TaskListFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //Define the layout of the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(context)
                .inflate(R.layout.item_tasks, parent, false)//Inflate layout of the recycler view
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
        )//Define params of the layout
        layoutParams.setMargins(15.toDP().toPx(), 0, 40.toDP().toPx(), 0)
        view.layoutParams = layoutParams

        return TaskListViewHolder(view)
    }

    //Call to display info on a recycler item
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = list[position]
        if (holder is TaskListViewHolder) {
            if (position == list.size - 1) {//If we are displaying the last element of the list, there will always be at least the addList entry
                holder.itemView.tv_add_task_list.visibility =
                    View.VISIBLE // Display the text view to add list
                holder.itemView.ll_task_item.visibility = View.GONE // Hide the list of task
            } else {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.ll_task_item.visibility = View.VISIBLE
            }

            holder.itemView.tv_task_list_title.text = task.title
            holder.itemView.tv_add_task_list.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.GONE
                holder.itemView.cv_add_task_list_name.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility = View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility = View.GONE
            }

            holder.itemView.ib_done_list_name.setOnClickListener {
                //TODO : create task and display Task list
                val taskListName = holder.itemView.et_task_list_name.text.toString()
                if (taskListName.isNotEmpty()) {
                    val activity = context as BaseActivity
                    activity.showProgressDialog()
                    createTaskList(taskListName)
                    frag.refreshFragment()
                    activity.hideProgressDialog()
                }
                Log.d("TaskListAdapter", "Create task")
            }
        }


    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, Firestore.getCurrentUserId())
        board.taskList.add(0, task)
        board.taskList.removeAt(board.taskList.size - 1)
        Firestore.updateBoardTaskList(board)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDP(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()


    class TaskListViewHolder(view: View) : RecyclerView.ViewHolder(view)

}