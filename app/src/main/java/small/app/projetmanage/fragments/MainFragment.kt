package small.app.projetmanage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.adapters.BoardItemsAdapter
import small.app.projetmanage.firebase.Firestore.Companion.loadBoardsList
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


    override fun onResume() {
        (requireActivity() as MainActivity).setActionBarTitle(resources.getString(R.string.app_name))

        loadBoardsList(this)
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
                BoardItemsAdapter.BoardItemsViewHolder.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    findNavController().navigate(
                        MainFragmentDirections.actionMainFragmentToTaskListFragment(
                            model
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