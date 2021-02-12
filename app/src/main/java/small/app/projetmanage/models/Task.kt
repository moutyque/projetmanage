package small.app.projetmanage.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task(
    var title: String = "",
    var documentId: String = "",
    val createdBy: String = ""
) : Parcelable {
}