package small.app.projetmanage.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import small.app.projetmanage.R
import small.app.projetmanage.databinding.FragmentCardDetailsBinding
import small.app.projetmanage.dialogs.LabelColorListDialog
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.Card
import small.app.projetmanage.models.CardModel

//TODO : remove it from the stack

//TODO : get the board member in the card
class CardDetailsFragment : Fragment() {

    val args: CardDetailsFragmentArgs by navArgs()
    private lateinit var card: Card
    private var position: Int = -1

    private var dialog: Dialog? = null

    private val colors =
        listOf("#43C86F", "#0C90F1", "#F72400", "#7A8089", "#D57C1D", "#770000", "#0022F8")

    private lateinit var model: CardModel
    private lateinit var binding: FragmentCardDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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

        binding.model = model
        model.name.value = card.name
        model.name.observe(viewLifecycleOwner, Observer {
            card.name = it
        })
        binding.etNameCardDetails.addTextChangedListener { text -> card.name = text.toString() }
        model.color.value = card.color
        model.color.observe(viewLifecycleOwner, Observer {
            if (dialog != null) dialog!!.dismiss()
            setColor()

        })

        //Update card info
        binding.btnUpdateCardDetails.setOnClickListener {
            findNavController().navigate(
                CardDetailsFragmentDirections.actionCardDetailsFragmentToTaskListFragment(
                    Firestore.updateCard(card, position)
                )
            )
        }

        //
        binding.tvSelectLabelColor.setOnClickListener {
            dialog =
                LabelColorListDialog(
                    requireContext(),
                    resources.getString(R.string.str_select_label_color),
                    colors,
                    model.color
                )
            (dialog as LabelColorListDialog).show()
        }


        // Inflate the layout for this fragment
        return binding.root
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