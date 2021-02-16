package small.app.projetmanage.adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tasks.view.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.fragments.TaskListFragment
import small.app.projetmanage.models.Board
import small.app.projetmanage.models.Card
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


            //Validate the new task name
            holder.itemView.ib_done_list_name.setOnClickListener {
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

            //Start editing the task
            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_task_list_name.setText(task.title)
                holder.itemView.ll_title_view.visibility = View.GONE
                holder.itemView.cv_edit_task_list_name.visibility = View.VISIBLE
            }

            //Close the editable text
            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility = View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility = View.GONE
            }

            //Validate the edition of the task name
            holder.itemView.ib_done_edit_list_name.setOnClickListener {
                val taskListName = holder.itemView.et_edit_task_list_name.text.toString()

                if (taskListName.isNotEmpty()) {
                    val activity = context as BaseActivity
                    activity.showProgressDialog()
                    updateTaskList(position, taskListName)
                    frag.refreshFragment()
                    activity.hideProgressDialog()
                }
            }

            //Delete task
            holder.itemView.ib_delete_list.setOnClickListener {
                val activity = context as BaseActivity
                activity.showProgressDialog()
                deleteTaskList(position)
                frag.refreshFragment()
                activity.hideProgressDialog()
            }


            //Display the card creation card view
            holder.itemView.tv_add_card.setOnClickListener {
                holder.itemView.tv_add_card.visibility = View.GONE
                holder.itemView.cv_add_card.visibility = View.VISIBLE
            }

            holder.itemView.ib_close_card_name.setOnClickListener {
                holder.itemView.tv_add_card.visibility = View.VISIBLE
                holder.itemView.cv_add_card.visibility = View.GONE
            }

            holder.itemView.ib_done_card_name.setOnClickListener {
                if (holder.itemView.et_card_name.text.isNotEmpty()) {
                    val activity = context as BaseActivity
                    activity.showProgressDialog()
                    addCardToTaskList(holder.itemView.et_card_name.text.toString(), position)
                    frag.refreshFragment()
                    activity.hideProgressDialog()
                }

            }

            holder.itemView.rv_card_list.layoutManager = LinearLayoutManager(context)

            holder.itemView.rv_card_list.setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, task.cards)
            holder.itemView.rv_card_list.adapter = adapter

        }


    }

    private fun addCardToTaskList(name: String, position: Int) {
        val card = Card(name = name, createdBy = Firestore.loginUser.value!!.name)
        card.assignedTo.add(Firestore.loginUser.value!!.uid)
        val task = board.taskList[position]
        task.cards.add(card)
        updateTaskList(task, position)

    }

    fun deleteTaskList(position: Int) {
        board.taskList.removeAt(position)
        board.taskList.removeAt(board.taskList.size - 1)
        Firestore.updateBoardTaskList(board)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, Firestore.getCurrentUserId())
        board.taskList.add(0, task)
        board.taskList.removeAt(board.taskList.size - 1)
        Firestore.updateBoardTaskList(board)

    }

    fun updateTaskList(position: Int, listName: String) {
        val task = board.taskList[position]
        task.title = listName
        updateTaskList(task, position)
    }

    private fun updateTaskList(task: Task, position: Int) {
        board.taskList[position] = task
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