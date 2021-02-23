package small.app.projetmanage.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card(
    var name: String = "",
    val createdBy: String = "",
    val assignedTo: MutableList<String> = ArrayList(),
    var color: String = ""
) : Parcelable
