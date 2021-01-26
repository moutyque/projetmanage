package small.app.projetmanage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {


        setupProfileToolbar()
        super.onResume()
    }

    fun setupProfileToolbar() {
        val mainActivity = requireActivity() as MainActivity
        val actionBar = mainActivity.supportActionBar
        actionBar!!.hide()
        //toolbar_main_activity.visibility = View.GONE

        mainActivity.setSupportActionBar(toolbar_my_profile_activity)
        toolbar_my_profile_activity.setNavigationIcon(R.drawable.ic_black_color_back_24dp)

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        toolbar_my_profile_activity.setNavigationOnClickListener {
            //toolbar_my_profile_activity.visibility = View.GONE

            mainActivity.setupDefaultActionBar()

            mainActivity.onBackPressed()

        }
    }

    //TODO : doesn't work, what happend when we click onBackPressed
    override fun onStop() {
        super.onStop()

        val mainActivity = requireActivity() as MainActivity
        val actionBar = mainActivity.supportActionBar
        actionBar!!.hide()
        mainActivity.setupDefaultActionBar()
        super.onPause()
    }


}