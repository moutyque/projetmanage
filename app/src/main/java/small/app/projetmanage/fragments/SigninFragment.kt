package small.app.projetmanage.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_signin.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.firebase.Firestore


/**
 * A simple [Fragment] subclass.
 * Use the [SigninFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SigninFragment : DefaultFragment() {
    private lateinit var activity: BaseActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity() as BaseActivity

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar_sign_in_activity.setNavigationIcon(R.drawable.ic_black_color_back_24dp)
        toolbar_sign_in_activity.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        btn_sign_in.setOnClickListener {
            activity.showProgressDialog(resources.getString(R.string.please_wait))
            signInUser()
            activity.hideProgressDialog()
        }
        super.onViewCreated(view, savedInstanceState)
    }


    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                (requireActivity() as BaseActivity).showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password) -> {
                (requireActivity() as BaseActivity).showErrorSnackBar("Please enter a password")
                false
            }
            else -> true
        }
    }

    private fun signInUser() {
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    activity.hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registerEmail = firebaseUser.email!!


                        Firestore.signInUser()
                        Firestore.loginUser.observe(viewLifecycleOwner, { result ->
                            if (result != null) {
                                requireView().findNavController()
                                    .navigate(SigninFragmentDirections.actionSigninFragmentToMainFragment())
                            }
                        })

                    } else {
                        activity.showErrorSnackBar(task.exception!!.message!!)
                    }
                }
        }

    }
}