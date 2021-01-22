package small.app.projetmanage.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_intro.*
import small.app.projetmanage.R


/**
 * A simple [Fragment] subclass.
 * Use the [IntroFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IntroFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_sign_up_intro.setOnClickListener {
            view.findNavController()
                .navigate(IntroFragmentDirections.actionIntroFragmentToSignupFragment())
        }
        btn_sign_in_intro.setOnClickListener {
            view.findNavController()
                .navigate(IntroFragmentDirections.actionIntroFragmentToSigninFragment())
        }

    }


}