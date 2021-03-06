package org.linphone.activities.main.history.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.HistoryDetailFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.*
import org.linphone.activities.main.MainActivity
import org.linphone.activities.main.history.viewmodels.CallLogViewModel
import org.linphone.activities.main.history.viewmodels.CallLogViewModelFactory
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.contact.NativeContact
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class DetailCallLogFragment : GenericFragment<HistoryDetailFragmentBinding>() {
    private lateinit var viewModel: CallLogViewModel
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int = R.layout.history_detail_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }
        binding.sharedMainViewModel = sharedViewModel

        val callLogGroup = sharedViewModel.selectedCallLogGroup.value
        if (callLogGroup == null) {
            Log.e("[History] Call log group is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        viewModel = ViewModelProvider(
            this,
            CallLogViewModelFactory(callLogGroup.lastCallLog)
        )[CallLogViewModel::class.java]
        binding.viewModel = viewModel

        useMaterialSharedAxisXForwardAnimation = sharedViewModel.isSlidingPaneSlideable.value == false

        viewModel.relatedCallLogs.value = callLogGroup.callLogs

        binding.setBackClickListener {
            goBack()
        }

        binding.setNewContactClickListener {
            val copy = viewModel.callLog.remoteAddress.clone()
            copy.clean()
            Log.i("[History] Creating contact with SIP URI: ${copy.asStringUriOnly()}")
            sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(R.id.masterCallLogsFragment)
            navigateToContacts(copy.asStringUriOnly())
        }

        binding.setContactClickListener {
            sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(R.id.masterCallLogsFragment)
            val contact = viewModel.contact.value as? NativeContact
            if (contact != null) {
                Log.i("[History] Displaying contact $contact")
                navigateToContact(contact)
            } else {
                val copy = viewModel.callLog.remoteAddress.clone()
                copy.clean()
                Log.i("[History] Displaying friend with address ${copy.asStringUriOnly()}")
                navigateToFriend(copy)
            }
        }

        viewModel.startCallEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { callLog ->
                    val address = callLog.remoteAddress
                    if (coreContext.core.callsNb > 0) {
                        Log.i("[History] Starting dialer with pre-filled URI ${address.asStringUriOnly()}, is transfer? ${sharedViewModel.pendingCallTransfer}")
                        sharedViewModel.updateDialerAnimationsBasedOnDestination.value = Event(R.id.masterCallLogsFragment)

                        val args = Bundle()
                        args.putString("URI", address.asStringUriOnly())
                        args.putBoolean("Transfer", sharedViewModel.pendingCallTransfer)
                        args.putBoolean(
                            "SkipAutoCallStart",
                            true
                        ) // If auto start call setting is enabled, ignore it
                        navigateToDialer(args)
                    } else {
                        val localAddress = callLog.localAddress
                        coreContext.startCall(address, localAddress = localAddress)
                    }
                }
            }
        )

        viewModel.chatRoomCreatedEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { chatRoom ->
                    val args = Bundle()
                    args.putString("LocalSipUri", chatRoom.localAddress.asStringUriOnly())
                    args.putString("RemoteSipUri", chatRoom.peerAddress.asStringUriOnly())
                    navigateToChatRoom(args)
                }
            }
        )

        viewModel.onErrorEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { messageResourceId ->
                    (activity as MainActivity).showSnackBar(messageResourceId)
                }
            }
        )
    }

    override fun goBack() {
        if (sharedViewModel.isSlidingPaneSlideable.value == true) {
            sharedViewModel.closeSlidingPaneEvent.value = Event(true)
        } else {
            navigateToEmptyCallHistory()
        }
    }
}
