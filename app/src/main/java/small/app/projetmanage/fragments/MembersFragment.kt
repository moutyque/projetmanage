package small.app.projetmanage.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONObject
import small.app.projetmanage.R
import small.app.projetmanage.activities.MainActivity
import small.app.projetmanage.adapters.MembersItemAdapter
import small.app.projetmanage.databinding.FragmentMembersBinding
import small.app.projetmanage.firebase.Firestore
import small.app.projetmanage.firebase.Firestore.Companion.getUserByEmail
import small.app.projetmanage.models.User
import small.app.projetmanage.utils.Constants
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MembersFragment : Fragment() {
    private lateinit var binding: FragmentMembersBinding
    val args: MembersFragmentArgs by navArgs()
    var members = MutableLiveData<ArrayList<User>>()
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
//A new member has been assigned to the board
        newMember.observe(viewLifecycleOwner, {

            args.board.assignedTo.add(it.uid)
            SendNotificationToUserAsyncTask(
                it.fcmToken,
                args.board.name
            ).execute()
            Firestore.updateBoardTaskList(args.board)
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
                    DialogInterface.OnClickListener { _, _ ->
                        email = input.text.toString()
                        getUserByEmail(email = email, user = newMember)
                    })
                builder.setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

                builder.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private inner class SendNotificationToUserAsyncTask(val token: String, val boardName: String) :
        AsyncTask<Any, Void, String>() {
        override fun doInBackground(vararg p0: Any?): String {
            var result: String
            var connection: HttpURLConnection? = null
            try {
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"

                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION,
                    "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )

                connection.useCaches = false

                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board ${boardName}")
                dataObject.put(
                    Constants.FCM_KEY_MESSAGE,
                    "You have been assigned to the board by ${Firestore.loginUser.value!!.name}"
                )

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()
                val httpResult: Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {

                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line + "\n")

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = sb.toString()


                } else {
                    result = connection.responseMessage.toString()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                result = "Error with : " + e.message
            } finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPreExecute() {
            (requireActivity() as MainActivity).showProgressDialog()
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            (requireActivity() as MainActivity).hideProgressDialog()
            super.onPostExecute(result)
        }

    }


}