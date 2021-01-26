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
import kotlinx.android.synthetic.main.fragment_signup.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.User

/**
 * A simple [Fragment] subclass.
 * Use the [SignupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var activity: BaseActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity = requireActivity() as BaseActivity
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar_sign_up_activity.setNavigationIcon(R.drawable.ic_black_color_back_24dp)
        toolbar_sign_up_activity.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        btn_sign_up.setOnClickListener {
            activity.showProgressDialog(resources.getString(R.string.please_wait))
            registerUser()
            activity.hideProgressDialog()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                (requireActivity() as BaseActivity).showErrorSnackBar("Please enter a name")
                false
            }
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

    private fun registerUser() {
        val name: String = et_name.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    activity.hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(email = email, name = name, uid = firebaseUser.uid)
                        Firestore.registerUser(activity, user)
                        requireView().findNavController()
                            .navigate(SignupFragmentDirections.actionSignupFragmentToMainFragment())

                    } else {
                        activity.showErrorSnackBar(task.exception!!.message!!)
                    }
                }
        }

    }
}