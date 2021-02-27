package small.app.projetmanage.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String = "",
    var isSelected: Boolean = false
) : Parcelable {

    companion object {
        const val IMAGE: String = "image"
        const val NAME: String = "name"
        const val MOBILE: String = "mobile"
    }

    fun toHashMap(): Map<String, Any> {
        val userHahMap = HashMap<String, Any>()
        userHahMap[IMAGE] = image
        userHahMap[NAME] = name
        userHahMap[MOBILE] = mobile
        return userHahMap
    }

}
  

