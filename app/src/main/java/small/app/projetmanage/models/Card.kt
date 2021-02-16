package small.app.projetmanage.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card(
    val name: String = "",
    val createdBy: String = "",
    val assignedTo: MutableList<String> = ArrayList()
) : Parcelable
