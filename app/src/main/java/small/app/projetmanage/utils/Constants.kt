package small.app.projetmanage.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

object Constants {


    const val FCM_TOKEN_UPDATED: String = "fcmTokenUpdated"
    const val FCM_TOKEN: String = "fcmToken"
    const val EMAIL = "email"
    const val USERS = "Users"
    const val BOARD = "Boards"
    const val ID = "uid"
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val ASSIGNED_TO = "assignedTo"

    const val FCM_BASE_URL: String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION: String = "authorization"
    const val FCM_KEY: String = "key"
    const val FCM_SERVER_KEY: String =
        "AAAAdsx3BfA:APA91bE2VyDSIhirWaLovvntmsftX0Hrr_nqggtgx57BvIyIr6Zw7PkycRMCFiNmFtIiNK-CrWYKeVU0akXT85vScMIAsD6Dwymr8FzofGVwNu5PIRhWrTZElpIDRw8FUYOFvXl-hoa0"
    const val FCM_KEY_TITLE: String = "title"
    const val FCM_KEY_MESSAGE: String = "message"
    const val FCM_KEY_DATA: String = "data"
    const val FCM_KEY_TO: String = "to"

    const val PROJEMANAGE_PREFRENCES = "ProjetManagePrefs"

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun showImagePicker(activity: Activity) {
        val galleryInt = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryInt, PICK_IMAGE_REQUEST_CODE)

    }

    fun showImagePicker(fragment: Fragment) {
        val galleryInt = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        fragment.startActivityForResult(galleryInt, PICK_IMAGE_REQUEST_CODE)

    }

    fun updatePictureInFragment(
        activity: Activity,
        imageAdress: String,
        placeHolder: Int,
        target: ImageView
    ) {
        Glide.with(activity).load(activity)
            .load(imageAdress)
            .centerCrop()
            .placeholder(placeHolder)
            .into(target)
    }


}