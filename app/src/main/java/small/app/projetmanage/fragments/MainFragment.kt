package small.app.projetmanage.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_main.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.adapters.BoardItemsAdapter
import small.app.projetmanage.adapters.OnClickListener
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.Board
import small.app.projetmanage.utils.Constants

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : DefaultFragment() {
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mSharedPreferences = requireActivity().getSharedPreferences(
            Constants.PROJEMANAGE_PREFRENCES,
            AppCompatActivity.MODE_PRIVATE
        )
        val tokenUpdate = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
        if (tokenUpdate) {
            (requireActivity() as MainActivity).showProgressDialog()
            Firestore.signInUser()//Not sure
            (requireActivity() as MainActivity).hideProgressDialog()
        } else {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(requireActivity()) {
                updateFCMToken(it.token)
            }
        }

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

    fun tokenUpdateSuccess() {
        (requireActivity() as MainActivity).hideProgressDialog()

        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        (requireActivity() as MainActivity).showProgressDialog()
        Firestore.signInUser()//Not sure what should be done here regarding the course code
        (requireActivity() as MainActivity).hideProgressDialog()
    }


    private fun updateFCMToken(token: String) {
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        (requireActivity() as MainActivity).showProgressDialog()
        Firestore.updateUserProfileData(userHashMap, requireActivity() as MainActivity)
        tokenUpdateSuccess()
        (requireActivity() as MainActivity).hideProgressDialog()

    }


}