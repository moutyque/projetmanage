package small.app.projetmanage.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task_list.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.adapters.TaskListItemsAdapter
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.Board
import small.app.projetmanage.models.Card
import small.app.projetmanage.models.Task
import small.app.projetmanage.models.User


class TaskListFragment : Fragment() {

    val args: TaskListFragmentArgs by navArgs()
    private lateinit var board: Board

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        board = args.board

        (requireActivity() as MainActivity).setActionBarTitle(board.name)

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

        val users = MutableLiveData<ArrayList<User>>()
        Firestore.getAssignedMembersListDetails(users, board.assignedTo)
        users.observe(viewLifecycleOwner, Observer {
            val adapter = TaskListItemsAdapter(
                requireContext(),
                board.taskList,
                board,
                this,
                it.toTypedArray()
            )
            rv_task_list.adapter = adapter
        })
        super.onResume()
    }


    fun refreshFragment() {

        this.parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_members, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                requireView().findNavController().navigate(
                    MembersFragmentDirections.actionGlobalMembersFragment(
                        board = board
                    )
                )

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateCardsInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {
        board.taskList.removeLast()
        board.taskList[taskListPosition].cards = cards
        (requireActivity() as MainActivity).showProgressDialog()
        Firestore.updateBoardTaskList(board)
        (requireActivity() as MainActivity).hideProgressDialog()
    }
}



