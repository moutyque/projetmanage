package small.app.projetmanage.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.projemanag.adapters.CardMemberListItemsAdapter
import small.app.projetmanage.R
import small.app.projetmanage.databinding.FragmentCardDetailsBinding
import small.app.projetmanage.dialogs.LabelColorListDialog
import small.app.projetmanage.dialogs.MembersListDialog
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.Card
import small.app.projetmanage.models.CardModel
import small.app.projetmanage.models.User
import java.util.function.Consumer

//TODO : remove it from the stack

class CardDetailsFragment : Fragment() {

    private val args: CardDetailsFragmentArgs by navArgs()
    private lateinit var card: Card
    private var position: Int = -1

    private var colorDialog: LabelColorListDialog? = null
    private var membersDialog: MembersListDialog? = null
    private val colors =
        listOf("#43C86F", "#0C90F1", "#F72400", "#7A8089", "#D57C1D", "#770000", "#0022F8")

    private lateinit var model: CardModel
    private lateinit var binding: FragmentCardDetailsBinding
    private lateinit var members: List<User>

    private lateinit var selectedMembersList: ArrayList<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        members = args.members.toList()
        card = args.card
        position = args.position



        setHasOptionsMenu(true)
        for (u in args.members) {
            Log.d("CardDetails", u.name)

        }
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        model = CardModel()
        Firestore.getAssignedMembersListDetails(model.user, card.assignedTo)
        model.user.observe(viewLifecycleOwner, Observer {
            selectedMembersList = ArrayList<User>(model.user.value!!)
            selectedMembersList.forEach(Consumer { u -> u.isSelected = true })

            selectedMembersList.add(User())

            setMember()
        })


        binding.model = model
        model.name.value = card.name
        model.name.observe(viewLifecycleOwner, Observer {
            card.name = it
        })
        binding.etNameCardDetails.addTextChangedListener { text -> card.name = text.toString() }
        model.color.value = card.color
        //Update color
        model.color.observe(viewLifecycleOwner, Observer {
            if (colorDialog != null) colorDialog!!.dismiss()
            setColor()
        })


        //Update card info
        binding.btnUpdateCardDetails.setOnClickListener {
            selectedMembersList.forEach(Consumer { u -> card.assignedTo.add(u.uid) })
            findNavController().navigate(
                CardDetailsFragmentDirections.actionCardDetailsFragmentToTaskListFragment(
                    Firestore.updateCard(card, position)
                )
            )
        }

        //Manage the color selection
        binding.tvSelectLabelColor.setOnClickListener {
            colorDialog =
                LabelColorListDialog(
                    requireContext(),
                    resources.getString(R.string.str_select_label_color),
                    colors,
                    model.color
                )
            colorDialog!!.show()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

//    override fun onResume() {
//        setMember()
//        val bindingAdapter =
//            (binding.rvCardSelectedMembersList.adapter as CardMemberListItemsAdapter)
//
//        bindingAdapter.notifyDataSetChanged()
//        super.onResume()
//    }

    private fun setMember() {
        if (model.user.value != null) {

            binding.rvCardSelectedMembersList.visibility = View.VISIBLE

            binding.rvCardSelectedMembersList.layoutManager =
                GridLayoutManager(requireActivity(), 6)


//TODO : Problème la liste n'est pas enregistrertester avec pls users

            val clickListener = object : CardMemberListItemsAdapter.OnClickListener {
                override fun onClick() {
                    val adapter =
                        (binding.rvCardSelectedMembersList.adapter as CardMemberListItemsAdapter)
                    var selectedUser = MutableLiveData<User>()
                    //Add new selected user
                    selectedUser.observe(viewLifecycleOwner, Observer {
                        //If the user was already selected you must remove it from the list elle
                        for (i in members.indices) {
                            if (members[i] == it) {
                                break
                            }
                        }
                        Log.d(
                            "AdapterChange", adapter.itemCount.toString()
                        )
                        if (it.isSelected) {
                            (model.user.value!! as ArrayList).add(0, it)
                            selectedMembersList.add(0, it)

                        } else {
                            (model.user.value!! as ArrayList).remove(it)
                            selectedMembersList.remove(it)


                        }
                        membersDialog?.dismiss()

                        adapter.notifyDataSetChanged()


                        Log.d(
                            "AdapterChange", adapter.itemCount.toString()
                        )
                    })
                    //TODO : Bug les membres déjà selectionné n'apparaissent pas comme tel dans le dialog
                    members.forEach { m ->
                        if (selectedMembersList.contains(m)) {
                            m.isSelected = true
                        }
                    }
                    membersDialog = MembersListDialog(
                        requireActivity(),
                        resources.getString(R.string.str_select_members),
                        members,
                        selectedUser
                    )
                    membersDialog!!.show()
                }
            }
            binding.rvCardSelectedMembersList.adapter =
                CardMemberListItemsAdapter(requireActivity(), selectedMembersList)
            (binding.rvCardSelectedMembersList.adapter as CardMemberListItemsAdapter).setOnClickListener(
                clickListener
            )


        }
    }


    private fun setColor() {
        if (!model.color.value.isNullOrEmpty()) {
            binding.tvSelectLabelColor.text = ""
            binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(model.color.value))
            card.color = model.color.value!!
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity()).actionBar?.title = card.name
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_delete_card, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                findNavController().navigate(
                    CardDetailsFragmentDirections.actionCardDetailsFragmentToTaskListFragment(
                        Firestore.updateCard(null, position)
                    )
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}