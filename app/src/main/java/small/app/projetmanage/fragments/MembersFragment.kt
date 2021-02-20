package small.app.projetmanage.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import small.app.projetmanage.R
import small.app.projetmanage.adapters.MembersItemAdapter
import small.app.projetmanage.databinding.FragmentMembersBinding
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.firebase.Firestore.Companion.getUserByEmail
import small.app.projetmanage.models.User


class MembersFragment : Fragment() {
    private lateinit var binding: FragmentMembersBinding
    val args: MembersFragmentArgs by navArgs()
    var members = MutableLiveData<List<User>>()
    var newMember = MutableLiveData<User>()
    private var email = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMembersBinding.inflate(layoutInflater)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvMembersList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMembersList.setHasFixedSize(true)
        setHasOptionsMenu(true)
        members.observe(viewLifecycleOwner, Observer {
            val adapater = MembersItemAdapter(requireActivity(), it)
            binding.rvMembersList.adapter = adapater

        })

        newMember.observe(viewLifecycleOwner, {
//            val value = members.value
//            val tmpList = ArrayList<User>()
//            for (u in members.value!!) {
//                tmpList.add(u)
//            }
//            tmpList.add(it)
//            members.value = tmpList
            args.board.assignedTo.add(it.uid)
            refreshList()
        })
        refreshList()
        return binding.root
    }

    override fun onResume() {
        refreshList()
        super.onResume()
    }

    private fun refreshList() {

        Firestore.getAssignedMembersListDetails(members, args.board.assignedTo)


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_member, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Email")
                val input = EditText(requireContext())
                input.inputType =
                    InputType.TYPE_CLASS_TEXT
                input.hint = "Email"
                builder.setView(input)

                builder.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, which ->
                        email = input.text.toString()
                        getUserByEmail(email = email, user = newMember)
                    })
                builder.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

                builder.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }


}