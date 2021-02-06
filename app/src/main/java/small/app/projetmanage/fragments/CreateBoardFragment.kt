package small.app.projetmanage.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_board.*
import small.app.projetmanage.R
import small.app.projetmanage.activities.BaseActivity
import small.app.projetmanage.databinding.FragmentCreateBoardBinding
import small.app.projetmanage.utils.Constants

class CreateBoardFragment : Fragment() {

    private lateinit var binding: FragmentCreateBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as BaseActivity).supportActionBar!!.title =
            resources.getString(R.string.creat_board_title)

        binding = FragmentCreateBoardBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_board_image.setOnClickListener {
            Constants.showImagePicker(requireActivity())
        }
    }

    //TODO : that code is never reach
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            Constants.updatePictureInFragment(
                requireActivity(),
                data!!.data.toString(),
                R.drawable.ic_board_place_holder,
                iv_board_image
            )
        }
    }

    override fun onPause() {
        (requireActivity() as BaseActivity).supportActionBar!!.title =
            resources.getString(R.string.app_name)
        super.onPause()
    }


}