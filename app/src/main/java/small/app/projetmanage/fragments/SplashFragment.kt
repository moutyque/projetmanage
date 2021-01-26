package small.app.projetmanage.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_splash.*
import small.app.projetmanage.R
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.firebase.Firestore.Companion.signInUser

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val typeFace = Typeface.createFromAsset(requireActivity().assets, "carbon bl.ttf")
        tv_app_name.typeface = typeFace
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({

            var currentUserId = Firestore.getCurrentUserId()
            if (!currentUserId.isNullOrEmpty()) {
                signInUser()
                view.findNavController()
                    .navigate(SplashFragmentDirections.actionSplashFragmentToMainFragment())
            } else {
                view.findNavController()
                    .navigate(SplashFragmentDirections.actionSplashFragmentToIntroFragment())
            }


        }, 2500)
    }


}