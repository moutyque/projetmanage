package small.app.projetmanage.models

import androidx.lifecycle.MutableLiveData

class CardModel {

    var name = MutableLiveData("")
    var color = MutableLiveData<String>()
    var user = MutableLiveData<ArrayList<User>>(ArrayList())

    fun removeUser(uuid: String) {
        val findFirst = user.value!!.stream().filter { u -> u.uid.contentEquals(uuid) }.findFirst()
        if (findFirst.isPresent) {
            user.value!!.remove(findFirst.get())
        }
    }


}