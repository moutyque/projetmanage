package small.app.projetmanage.firebase

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.fragments.MainFragment
import small.app.projetmanage.models.Board
import small.app.projetmanage.models.User
import small.app.projetmanage.utils.Constants
import small.app.projetmanage.utils.Constants.ASSIGNED_TO
import small.app.projetmanage.utils.Constants.BOARD
import small.app.projetmanage.utils.Constants.USERS

class Firestore {


    companion object {
        private val mFirestore = FirebaseFirestore.getInstance()
        var loginUser = MutableLiveData<User>()

        fun updateUserProfileData(userMap: HashMap<String, Any>, activity: MainActivity) {
            val image = MutableLiveData<String>()
            uploadAndUpdatePicture(
                image = userMap[User.IMAGE] as String,
                activity = activity,
                result = image
            )
            image.observe(activity, Observer {


                userMap.put(User.IMAGE, it)
                mFirestore.collection(USERS).document(getCurrentUserId()).update(userMap)
                    .addOnSuccessListener {
                        Log.i("Update", "The update happend")
                    }
            })
        }

        fun registerUser(activity: MainActivity, userInfo: User) {
            mFirestore.collection(USERS).document(getCurrentUserId())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    userRegisteredSuccess(activity)

                }.addOnFailureListener { e ->
                    Log.e(activity.javaClass.simpleName, "Error writing document.")
                }
        }

        fun createBoard(activity: MainActivity, boardInfo: Board) {
            val image = MutableLiveData<String>()
            uploadAndUpdatePicture(image = boardInfo.image, activity = activity, result = image)
            image.observe(activity, Observer {
                val bInfo = Board(
                    name = boardInfo.name,
                    createdBy = boardInfo.createdBy,
                    assignedTo = boardInfo.assignedTo,
                    image = it
                )

                mFirestore.collection(BOARD)
                    .document()
                    .set(bInfo, SetOptions.merge())
                    .addOnSuccessListener {
                        createBoardSuccess(activity)

                    }.addOnFailureListener { e ->
                        Log.e(activity.javaClass.simpleName, "Error writing document.")
                    }

            })

        }

        fun updateBoardTaskList(boardInfo: Board) {
            mFirestore.collection(BOARD)
                .document(boardInfo.documentId)
                .update(boardInfo.toHashMap())
                .addOnSuccessListener {

                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Error writing document.")
                }
        }

        private fun createBoardSuccess(activity: MainActivity) {
            Toast.makeText(
                activity.applicationContext,
                "You have successfully registered",
                Toast.LENGTH_SHORT
            ).show()
            activity.navController.navigate(R.id.mainFragment)

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

        fun updateUserInfo(user: User) {
            loginUser.value = user
        }


        fun getCurrentUserId(): String {
            var currentUser = getInstance().currentUser
            var currentUserId = ""
            if (currentUser != null) {
                currentUserId = currentUser.uid
            }

            return currentUserId
        }

        private fun userRegisteredSuccess(activity: MainActivity) {
            Toast.makeText(
                activity.applicationContext,
                "You have successfully registered",
                Toast.LENGTH_SHORT
            ).show()

            activity.navController.navigate(R.id.mainFragment)

            //getInstance().signOut()
        }

        /**
         *This methode wait for the end of the upload to end : can be improve later with a workManager
         * @param image:  the MutableLiveData that contain the local path of the picture and update it with the uploaded image link
         * @param activity : use for the firebase methode and to toast if failure
         *
         *
         */
        fun uploadAndUpdatePicture(
            image: String,
            activity: MainActivity,
            result: MutableLiveData<String>
        ) {
            if (!image.isEmpty()) {
                val uri = Uri.parse(image)

                val sRef = FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                        activity,
                        uri
                    )
                )

                val putFile = sRef.putFile(uri)
                putFile.addOnSuccessListener { it ->
                    Log.d("UploadFile", it.metadata!!.reference!!.downloadUrl.toString())
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        Log.d("UploadFile", uri.toString())
                        result.value = uri.toString()

                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        activity.applicationContext,
                        exception.message,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            } else {
                result.value = ""
            }

        }

        fun loadBoardsList(fragment: MainFragment) {
            mFirestore.collection(BOARD).whereArrayContains(ASSIGNED_TO, getCurrentUserId()).get()
                .addOnSuccessListener { document ->
                    Log.i(fragment.javaClass.simpleName, document.documents.toString())
                    val boardList: ArrayList<Board> = ArrayList()
                    for (i in document.documents) {
                        val board = i.toObject(Board::class.java)!!
                        board.documentId = i.id
                        boardList.add(board)

                    }
                    fragment.populateBoardListToUI(boardList)
                }
                .addOnFailureListener { e ->
                    Log.e(
                        fragment.javaClass.simpleName,
                        "Error while createing the board list ${e.message}"
                    )

                }

        }


    }//End of companion object
}