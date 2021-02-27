package small.app.projetmanage.models

import androidx.lifecycle.MutableLiveData

class CardModel {

    var name = MutableLiveData("")
    var color = MutableLiveData<String>()
    var user = MutableLiveData<List<User>>(ArrayList())

}