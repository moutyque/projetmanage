package small.app.projetmanage.fragments

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.adapters.BoardItemsAdapter
import small.app.projetmanage.adapters.OnClickListener
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.Board

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : DefaultFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).setNavController(findNavController())
        fab_create_board.setOnClickListener {
            requireView().findNavController().navigate(R.id.createBoardFragment)

        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {

        (requireActivity() as MainActivity).setActionBarTitle(resources.getString(R.string.app_name))
        Firestore.loadBoardsList(this)
        super.onResume()
    }


    fun populateBoardListToUI(boardsList: ArrayList<Board>) {
        if (!boardsList.isEmpty()) {
            rv_boards_list.visibility = View.VISIBLE
            tv_no_boards_available.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(requireContext())
            rv_boards_list.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(requireContext(), boardsList)
            rv_boards_list.adapter = adapter
            adapter.setOnClickListener(object :
                OnClickListener {
                override fun onClick(model: Parcelable) {
                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToTaskListFragment(
                            model as Board
                        )
                    )
                }

            })


        } else {
            rv_boards_list.visibility = View.GONE
            tv_no_boards_available.visibility = View.VISIBLE
        }
    }


}