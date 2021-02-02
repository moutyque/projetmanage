package small.app.projetmanage.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.databinding.FragmentProfileBinding
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.UserModel


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {


    private lateinit var binding: FragmentProfileBinding
    private var privateUrl = ""

    private var pictureUpdate = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val user = UserModel()
        binding = FragmentProfileBinding.inflate(layoutInflater)
        binding.user = user


        binding.lifecycleOwner = viewLifecycleOwner


        //When the user image change the picture is updated in the view
        user.image.observe(viewLifecycleOwner, Observer {
            Glide.with(this).load(this)
                .load(it)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivUserImage)
        })

        binding.ivUserImage.setOnClickListener {
            pickPicture()
        }
        binding.etMobile.doOnTextChanged { text, start, before, count ->
            user.mobile.value = text.toString().toLong()
        }

        binding.btnUpdate.setOnClickListener {
            if (pictureUpdate) uploadUserImage()
            Firestore.updateUserProfileData(
                user.toUser().toHashMap()
            )
            (requireActivity() as ComponentActivity).onBackPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginUser = Firestore.loginUser.value!!

        binding.user!!.email.value = loginUser.email
        binding.user!!.name.value = loginUser.name
        binding.user!!.mobile.value = loginUser.mobile
        binding.user!!.image.value = loginUser.image
        binding.etMobile.setText(loginUser.mobile.toString())


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == BaseActivity.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            binding.user!!.image.value = data.data.toString()
            pictureUpdate = true
        }
    }

    override fun onResume() {
        Firestore.loginUser

        setupProfileToolbar()
        super.onResume()
    }

    private fun setupProfileToolbar() {
        val mainActivity = requireActivity() as MainActivity
        val actionBar = mainActivity.supportActionBar
        actionBar!!.hide()
        //toolbar_main_activity.visibility = View.GONE
        mainActivity.setSupportActionBar(toolbar_my_profile_activity)
        toolbar_my_profile_activity.setNavigationIcon(R.drawable.ic_black_color_back_24dp)


        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        actionBar.title = resources.getString(R.string.my_profile_title)


        toolbar_my_profile_activity.setNavigationOnClickListener {
            //toolbar_my_profile_activity.visibility = View.GONE
            Log.d("ProfilToolBar", "Clicked on toolbar_my_profile_activity")
            mainActivity.setupDefaultActionBar()

            mainActivity.onBackPressed()

        }
    }

    override fun onStop() {
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setupDefaultActionBar()
        val actionBar = mainActivity.supportActionBar
        actionBar!!.show()
        super.onStop()
    }

    private fun pickPicture() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val galleryInt =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryInt, BaseActivity.PICK_IMAGE_REQUEST_CODE)
        } else {
            requireActivity().requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                BaseActivity.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun uploadUserImage() {
        (requireActivity() as MainActivity).showProgressDialog(resources.getString(R.string.please_wait))
        if (binding.user!!.image.value != null) {
            val uri = Uri.parse(binding.user!!.image.value)
            val sRef = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(uri)
            )
            sRef.putFile(uri).addOnSuccessListener { it ->
                Log.d("UploadFile", it.metadata!!.reference!!.downloadUrl.toString())
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("UploadFile", uri.toString())
                    binding.user!!.image.value = uri.toString()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_LONG).show()
            }
        }
        (requireActivity() as MainActivity).hideProgressDialog()

    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(requireActivity().contentResolver.getType(uri!!))
    }

}