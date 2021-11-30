package org.linphone.activities.main.contact.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ContactDetailFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.*
import org.linphone.activities.main.MainActivity
import org.linphone.activities.main.contact.viewmodels.ContactViewModel
import org.linphone.activities.main.contact.viewmodels.ContactViewModelFactory
import org.linphone.activities.main.viewmodels.DialogViewModel
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.DialogUtils
import org.linphone.utils.Event

class DetailContactFragment : GenericFragment<ContactDetailFragmentBinding>() {
    private lateinit var viewModel: ContactViewModel
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int = R.layout.contact_detail_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }
        binding.sharedMainViewModel = sharedViewModel

        useMaterialSharedAxisXForwardAnimation = sharedViewModel.isSlidingPaneSlideable.value == false

        val id = arguments?.getString("id")
        arguments?.clear()
        if (id != null) {
            Log.i("[Contact] Found contact id parameter in arguments: $id")
            sharedViewModel.selectedContact.value = coreContext.contactsManager.findContactById(id)
        }

        val contact = sharedViewModel.selectedContact.value
        if (contact == null) {
            Log.e("[Contact] Contact is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            goBack()
            return
        }

        viewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(contact)
        )[ContactViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.sendSmsToEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { number ->
                    sendSms(number)
                }
            }
        )

        viewModel.startCallToEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { address ->
                    if (coreContext.core.callsNb > 0) {
                        Log.i("[Contact] Starting dialer with pre-filled URI ${address.asStringUriOnly()}, is transfer? ${sharedViewModel.pendingCallTransfer}")
                        sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(R.id.dialerFragment)
                        sharedViewModel.updateDialerAnimationsBasedOnDestination.value = Event(R.id.masterContactsFragment)

                        val args = Bundle()
                        args.putString("URI", address.asStringUriOnly())
                        args.putBoolean("Transfer", sharedViewModel.pendingCallTransfer)
                        args.putBoolean("SkipAutoCallStart", true) // If auto start call setting is enabled, ignore it
                        navigateToDialer(args)
                    } else {
                        coreContext.startCall(address)
                    }
                }
            }
        )

        viewModel.chatRoomCreatedEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { chatRoom ->
                    sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(R.id.masterChatRoomsFragment)
                    val args = Bundle()
                    args.putString("LocalSipUri", chatRoom.localAddress.asStringUriOnly())
                    args.putString("RemoteSipUri", chatRoom.peerAddress.asStringUriOnly())
                    navigateToChatRoom(args)
                }
            }
        )

        binding.setBackClickListener {
            goBack()
        }

        binding.setEditClickListener {
            navigateToContactEditor()
        }

        binding.setDeleteClickListener {
            confirmContactRemoval()
        }

        viewModel.onErrorEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { messageResourceId ->
                    (activity as MainActivity).showSnackBar(messageResourceId)
                }
            }
        )

        view.doOnPreDraw {
            // Notifies fragment is ready to be drawn
            sharedViewModel.contactFragmentOpenedEvent.value = Event(true)
        }
    }

    override fun goBack() {
        if (!findNavController().popBackStack()) {
            if (sharedViewModel.isSlidingPaneSlideable.value == true) {
                sharedViewModel.closeSlidingPaneEvent.value = Event(true)
            } else {
                navigateToEmptyContact()
            }
        }
    }

    private fun confirmContactRemoval() {
        val dialogViewModel = DialogViewModel(getString(R.string.contact_delete_one_dialog))
        val dialog: Dialog = DialogUtils.getDialog(requireContext(), dialogViewModel)

        dialogViewModel.showCancelButton {
            dialog.dismiss()
        }

        dialogViewModel.showDeleteButton(
            {
                viewModel.deleteContact()
                dialog.dismiss()
                goBack()
            },
            getString(R.string.dialog_delete)
        )

        dialog.show()
    }

    private fun sendSms(number: String) {
        val smsIntent = Intent(Intent.ACTION_SENDTO)
        smsIntent.putExtra("address", number)
        smsIntent.data = Uri.parse("smsto:$number")
        val text = getString(R.string.contact_send_sms_invite_text).format(getString(R.string.contact_send_sms_invite_download_link))
        smsIntent.putExtra("sms_body", text)
        startActivity(smsIntent)
    }
}
