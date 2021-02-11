package small.app.projetmanage.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.models.Board

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }


}
