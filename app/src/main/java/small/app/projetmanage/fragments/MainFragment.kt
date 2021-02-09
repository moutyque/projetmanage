package small.app.projetmanage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import small.app.projetmanage.R
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadBoardsList(this)
    }

    fun populateBoardListToUI(boardsList: ArrayList<Board>) {
        if (!boardsList.isEmpty()) {
            rv_boards_list.visibility = View.VISIBLE
            tv_no_boards_available.visibility = View.GONE

            rv_boards_list.layoutManager = LinearLayoutManager(requireContext())
            rv_boards_list.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(requireContext(), boardsList)
            rv_boards_list.adapter = adapter


        } else {
            rv_boards_list.visibility = View.GONE
            tv_no_boards_available.visibility = View.VISIBLE
        }
    }


}