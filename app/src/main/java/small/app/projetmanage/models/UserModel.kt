package small.app.projetmanage.models

import androidx.lifecycle.MutableLiveData

class UserModel {

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val image = MutableLiveData<String>()
    val mobile = MutableLiveData<Long>()

    fun toUser(): User {

        return User(
            name = name.value!!,
            email = email.value!!,
            mobile = mobile.value!!,
            image = image.value!!
        )
    }


}