package small.app.projetmanage.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task_list.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.adapters.TaskListItemsAdapter
import small.app.projetmanage.models.Board
import small.app.projetmanage.models.Task


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskListFragment : Fragment() {

    val args: TaskListFragmentArgs by navArgs()
    private lateinit var board: Board

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        board = args.board

        (requireActivity() as MainActivity).setActionBarTitle(board.name)
        Log.d("TaskList", "We get board : ${args.board.name}")
        Log.d("TaskList", "its id is : ${args.board.documentId}")

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        rv_task_list.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rv_task_list.setHasFixedSize(true)

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        //Task for the button, it's a trick
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        val adapter = TaskListItemsAdapter(requireContext(), board.taskList, board, this)
        rv_task_list.adapter = adapter
        super.onResume()
    }


    fun refreshFragment() {

        this.parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_members, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}



