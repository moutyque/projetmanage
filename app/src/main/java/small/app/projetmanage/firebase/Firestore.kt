package small.app.projetmanage.firebase

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.models.User
import small.app.projetmanage.utils.Constants.USERS

class Firestore {


    companion object {
        private val mFirestore = FirebaseFirestore.getInstance()
        var loginUser = MutableLiveData<User>()

        fun updateUserProfileData(userMap: Map<String, Any>): Boolean {
            var returnVal = false
            mFirestore.collection(USERS).document(getCurrentUserId()).update(userMap)
                .addOnSuccessListener {
                    Log.i("Update", "The update happend")
                    returnVal = true
                }

            return returnVal
        }

        fun registerUser(activity: BaseActivity, userInfo: User) {
            mFirestore.collection(USERS).document(getCurrentUserId())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    userRegisteredSuccess(activity)

                }.addOnFailureListener { e ->
                    Log.e(activity.javaClass.simpleName, "Error writting document.")
                }
        }

        fun signInUser() {
            mFirestore.collection(USERS).document(getCurrentUserId())
                .get()
                .addOnSuccessListener { document ->
                    loginUser.value = document.toObject(User::class.java)
                }.addOnFailureListener { e ->
                    Log.e(Firestore::class.java.simpleName, "Error getting document.")
                }
        }


        fun getCurrentUserId(): String {
            var currentUser = getInstance().currentUser
            var currentUserId = ""
            if (currentUser != null) {
                currentUserId = currentUser.uid
            }

            return currentUserId
        }

        private fun userRegisteredSuccess(activity: BaseActivity) {
            Toast.makeText(
                activity.applicationContext,
                "You have successfully registered",
                Toast.LENGTH_SHORT
            ).show()

            getInstance().signOut()
        }
    }
}