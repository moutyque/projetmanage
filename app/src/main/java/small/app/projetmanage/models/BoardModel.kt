package small.app.projetmanage.models

import androidx.lifecycle.MutableLiveData
import small.app.projetmanage.firebase.Firestore

class BoardModel {
    val name = MutableLiveData<String>()
    val image = MutableLiveData<String>()

    /*
     val name : String ="",
    val image : String="",
    val createdBy : String ="",
    val assignedTo: ArrayList<String> = ArrayList()
     */

    fun toBoard(): Board {

        val list = ArrayList<String>()
        list.add(Firestore.loginUser.value!!.uid)
        val img = if (image.value == null) "" else image.value

        return Board(
            name = name.value!!,
            image = img!!,
            createdBy = Firestore.loginUser.value!!.name,
            assignedTo = list
        )
    }
}