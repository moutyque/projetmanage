package small.app.projetmanage.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_create_board.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.databinding.FragmentCreateBoardBinding
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.BoardModel
import small.app.projetmanage.utils.Constants

class CreateBoardFragment : Fragment() {

    private lateinit var binding: FragmentCreateBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as BaseActivity).supportActionBar!!.title =
            resources.getString(R.string.creat_board_title)

        binding = FragmentCreateBoardBinding.inflate(inflater)
        val model = BoardModel()
        binding.model = model
        binding.lifecycleOwner = viewLifecycleOwner

        binding.etBoardName.doOnTextChanged { text, _, _, _ -> model.name.value = text.toString() }

        model.image.observe(viewLifecycleOwner, Observer {
            Constants.updatePictureInFragment(
                requireActivity(),
                it,
                R.drawable.ic_board_place_holder,
                iv_board_image
            )
        })
        binding.ivBoardImage.setOnClickListener {
            Constants.showImagePicker(this)
        }

        val success = MutableLiveData<Boolean>(false)
        binding.btnCreate.setOnClickListener {
            Firestore.createBoard(requireActivity() as MainActivity, model.toBoard(), success)
        }

        success.observe(viewLifecycleOwner, Observer {
            if (it) {
                (requireActivity() as MainActivity).navigateFirstTabWithClearStack(R.id.mainFragment)


            }
        })


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("CreateBoardFragement", "onActivityResult")

        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {

            binding.model!!.image.value = data.data.toString()

        }
    }

    override fun onPause() {
        (requireActivity() as BaseActivity).supportActionBar!!.title =
            resources.getString(R.string.app_name)

        super.onPause()
    }

    override fun onDestroy() {

        super.onDestroy()
    }


}