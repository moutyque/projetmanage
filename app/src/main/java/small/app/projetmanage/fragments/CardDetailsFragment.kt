package small.app.projetmanage.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
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
import java.time.*
import java.time.temporal.ChronoField
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

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

    @RequiresApi(Build.VERSION_CODES.N)
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
            selectedMembersList.removeLast()
            card.assignedTo.clear()
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

        if (card.dueDate > 0) {
            val date: LocalDate =
                Instant.ofEpochMilli(card.dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
            binding.tvSelectDueDate.text =
                String.format("${date.year}/${date.monthValue}/${date.dayOfMonth}")
        }

        //Manage due date
        binding.tvSelectDueDate.setOnClickListener {
            val cldr = Calendar.getInstance()
            val day = cldr.get(Calendar.DAY_OF_MONTH)
            val month = cldr.get(Calendar.MONTH)
            val year = cldr.get(Calendar.YEAR)
            // date picker dialog
            val picker = DatePickerDialog(
                requireActivity(),
                { _, year, month, day ->
                    val dateTime =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            LocalDateTime.of(year, month, day, 0, 0, 0)
                        } else {
                            TODO("VERSION.SDK_INT < O")
                        }

                    binding.tvSelectDueDate.text = String.format("$year/$month/$day")
                    card.dueDate = dateTime.toMilliSeconds()
                },
                year,
                month,
                day
            )
            picker.show()

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

            binding.rvSelectedMembersList.visibility = View.VISIBLE

            binding.rvSelectedMembersList.layoutManager =
                GridLayoutManager(requireActivity(), 6)


//TODO : Problème la liste n'est pas enregistrertester avec pls users

            val clickListener = object : CardMemberListItemsAdapter.OnClickListener {
                override fun onClick() {
                    val adapter =
                        (binding.rvSelectedMembersList.adapter as CardMemberListItemsAdapter)
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
                            model.user.value!!.add(0, it)
                        } else {
                            model.removeUser(it.uid)
                        }
                        selectedMembersList.clear()
                        selectedMembersList.addAll(model.user.value!!)
                        selectedMembersList.add(User())
                        membersDialog?.dismiss()

                        adapter.notifyDataSetChanged()


                        Log.d(
                            "AdapterChange", adapter.itemCount.toString()
                        )
                    })
                    //TODO : Bug les membres déjà selectionné n'apparaissent pas comme tel dans le dialog
                    members.forEach { m ->
                        m.isSelected = false
                        if (selectedMembersList.stream().map { u -> u.uid }
                                .filter { u -> u.equals(m.uid) }.count().compareTo(
                                    1
                                ) == 0
                        ) {
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
            binding.rvSelectedMembersList.adapter =
                CardMemberListItemsAdapter(requireActivity(), selectedMembersList)
            (binding.rvSelectedMembersList.adapter as CardMemberListItemsAdapter).setOnClickListener(
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

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalDateTime.toMilliSeconds(): Long {
    return (this.toEpochSecond(ZoneOffset.UTC) * 1000
            + this.get(ChronoField.MILLI_OF_SECOND))


}
