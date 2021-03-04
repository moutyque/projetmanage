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
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.fragments.MainFragment
import small.app.projetmanage.models.Board
import small.app.projetmanage.models.Card
import small.app.projetmanage.models.Task
import small.app.projetmanage.models.User
import small.app.projetmanage.utils.Constants
import small.app.projetmanage.utils.Constants.ASSIGNED_TO
import small.app.projetmanage.utils.Constants.BOARD
import small.app.projetmanage.utils.Constants.USERS

class Firestore {


    companion object {
        private fun getFirestore(): FirebaseFirestore {
            return FirebaseFirestore.getInstance()
        }

        var loginUser = MutableLiveData<User>()

        lateinit var _board: Board
        lateinit var _task: Task

        fun setBoardAndTask(board: Board, task: Task) {
            _board = board
            _task = task
        }

        fun updateUserProfileData(userMap: HashMap<String, Any>, activity: MainActivity) {
            if (getCurrentUserId().isNullOrEmpty()) return
            //If there is an image in the map
            if (userMap[User.IMAGE] != null) {
                val image = MutableLiveData<String>()
                uploadAndUpdatePicture(
                    image = userMap[User.IMAGE] as String,
                    activity = activity,
                    result = image
                )
                image.observe(activity, Observer {


                    userMap.put(User.IMAGE, it)
                    getFirestore().collection(USERS).document(getCurrentUserId()).update(userMap)
                        .addOnSuccessListener {
                            Log.i("Update", "The update happend")
                        }
                })
            } else {
                getFirestore().collection(USERS).document(getCurrentUserId()).update(userMap)
                    .addOnSuccessListener {
                        Log.i("Update", "The update happend")
                    }
            }

        }

        fun registerUser(userInfo: User) {
            getFirestore().collection(USERS).document(getCurrentUserId())
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    Log.e("RegisterUser", "Succes to register the user.")
                    signInUser()
                }.addOnFailureListener { e ->
                    Log.e("RegisterUser", "Error writing document.")
                }
        }

        fun createBoard(
            activity: MainActivity,
            boardInfo: Board,
            success: MutableLiveData<Boolean>
        ) {
            val image = MutableLiveData<String>()
            uploadAndUpdatePicture(image = boardInfo.image, activity = activity, result = image)
            image.observe(activity, Observer {
                val bInfo = Board(
                    name = boardInfo.name,
                    createdBy = boardInfo.createdBy,
                    assignedTo = boardInfo.assignedTo,
                    image = it
                )

                getFirestore().collection(BOARD)
                    .document()
                    .set(bInfo, SetOptions.merge())
                    .addOnSuccessListener {
                        success.value = true

                    }.addOnFailureListener { e ->
                        Log.e("CreateBoard", "Error writing document.")
                    }

            })

        }

        fun updateBoardTaskList(boardInfo: Board) {
            getFirestore().collection(BOARD)
                .document(boardInfo.documentId)
                .update(boardInfo.toHashMap())
                .addOnSuccessListener {
                    Log.d("UpdateBoard", "Success to update board")

                }.addOnFailureListener { e ->
                    Log.e("UpdateBoard", "Error updating board.")
                }
        }


        fun signInUser() {
            getFirestore().collection(USERS).document(getCurrentUserId())
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
            getFirestore().collection(BOARD).whereArrayContains(ASSIGNED_TO, getCurrentUserId())
                .get()
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


        fun getAssignedMembersListDetails(
            members: MutableLiveData<ArrayList<User>>,
            assignedTo: ArrayList<String>
        ) {
            if (assignedTo.isEmpty()) {
                members.value = ArrayList()

            } else {
                getFirestore().collection(USERS) // Collection Name
                    .whereIn(
                        Constants.ID,
                        assignedTo
                    ) // Here the database field name and the id's of the members.
                    .get()
                    .addOnSuccessListener { document ->
                        Log.e("GetBoardUser", document.documents.toString())

                        val usersList: ArrayList<User> = ArrayList()

                        for (i in document.documents) {
                            // Convert all the document snapshot to the object using the data model class.
                            val user = i.toObject(User::class.java)!!
                            usersList.add(user)
                        }

                        members.value = usersList
                    }
                    .addOnFailureListener { e ->
                        Log.e(
                            "GetBoardUser",
                            "Error while creating a board.",
                            e
                        )
                    }
            }

        }

        fun getUserByEmail(
            user: MutableLiveData<User>,
            email: String
        ) {

            getFirestore().collection(USERS) // Collection Name
                .whereEqualTo(
                    Constants.EMAIL,
                    email
                )// Here the database field name and the id's of the members.
                .get()
                .addOnSuccessListener { document ->

                    if (!document.isEmpty) {
                        user.value = document.documents[0].toObject(User::class.java)!!
                    }

                }
                .addOnFailureListener { e ->
                    Log.e(
                        "getUserByEmail",
                        "Error while getting user by email.",
                        e
                    )
                }
        }

        fun updateCard(card: Card?, position: Int): Board {
            val taskIndex = _board.taskList.indexOf(_task)
            _task.cards.removeAt(position)
            if (card != null) {
                _task.cards.add(position, card)
            }
            _board.taskList.removeAt(taskIndex)
            _board.taskList.add(taskIndex, _task)
            updateBoardTaskList(_board)
            return _board

        }


    }//End of companion object
}