package small.app.projetmanage.fragments

import androidx.fragment.app.Fragment
import small.app.projetmanage.activities.MainActivity


open class DefaultFragment : Fragment() {

    /*
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_default, container, false)
        }
    */
    override fun onResume() {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setupDefaultActionBar()

        super.onResume()
    }
}