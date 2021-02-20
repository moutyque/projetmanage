package small.app.projetmanage.fragments

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import small.app.projetmanage.R
import small.app.projetmanage.databinding.FragmentCardDetailsBinding
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.models.Card
import small.app.projetmanage.models.CardModel


class CardDetailsFragment : Fragment() {

    val args: CardDetailsFragmentArgs by navArgs()
    private lateinit var card: Card
    private var position: Int = -1

    private lateinit var model: CardModel
    private lateinit var binding: FragmentCardDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        card = args.card
        position = args.position
        setHasOptionsMenu(true)

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

        binding.btnUpdateCardDetails.setOnClickListener {

            findNavController().navigate(
                CardDetailsFragmentDirections.actionCardDetailsFragmentToTaskListFragment(
                    Firestore.updateCard(card, position)
                )
            )
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity()).actionBar?.setTitle(card.name)
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