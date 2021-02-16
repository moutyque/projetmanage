package small.app.projetmanage.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Board(
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    var documentId: String = "",
    var taskList: ArrayList<Task> = ArrayList()
) : Parcelable {

    companion object {
        const val IMAGE: String = "image"
        const val NAME: String = "name"
        const val CREATED_BY: String = "createdBy"
        const val ASSIGNED_TO: String = "assignedTo"
        const val TASK_LIST: String = "taskList"
    }

    fun toHashMap(): Map<String, Any> {
        val boardHashmap = HashMap<String, Any>()
        boardHashmap[IMAGE] = image
        boardHashmap[NAME] = name
        boardHashmap[CREATED_BY] = createdBy
        boardHashmap[ASSIGNED_TO] = assignedTo
        boardHashmap[TASK_LIST] = taskList

        return boardHashmap
    }
}
