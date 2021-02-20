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

    const val EMAIL = "email"
    const val USERS = "Users"
    const val BOARD = "Boards"
    const val ID = "uid"
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val ASSIGNED_TO = "assignedTo"

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