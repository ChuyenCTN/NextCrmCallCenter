package org.linphone.activities.main.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.TabsFragmentBinding
import org.linphone.activities.*
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.activities.main.viewmodels.TabsViewModel
import org.linphone.utils.Event

class TabsFragment : GenericFragment<TabsFragmentBinding>(), NavController.OnDestinationChangedListener {
    private lateinit var viewModel: TabsViewModel
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int = R.layout.tabs_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        useMaterialSharedAxisXForwardAnimation = false

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        viewModel = requireActivity().run {
            ViewModelProvider(this).get(TabsViewModel::class.java)
        }
        binding.viewModel = viewModel

        binding.setHistoryClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.masterContactsFragment -> sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(R.id.masterCallLogsFragment)
                R.id.dialerFragment -> sharedViewModel.updateDialerAnimationsBasedOnDestination.value = Event(R.id.masterCallLogsFragment)
            }
            navigateToCallHistory()
        }

        binding.setContactsClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.dialerFragment -> sharedViewModel.updateDialerAnimationsBasedOnDestination.value = Event(R.id.masterContactsFragment)
            }
            sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(findNavController().currentDestination?.id ?: -1)
            navigateToContacts()
        }

        binding.setDialerClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.masterContactsFragment -> sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(R.id.dialerFragment)
            }
            sharedViewModel.updateDialerAnimationsBasedOnDestination.value = Event(findNavController().currentDestination?.id ?: -1)
            navigateToDialer()
        }

        binding.setChatClickListener {
            when (findNavController().currentDestination?.id) {
                R.id.masterContactsFragment -> sharedViewModel.updateContactsAnimationsBasedOnDestination.value = Event(R.id.masterChatRoomsFragment)
                R.id.dialerFragment -> sharedViewModel.updateDialerAnimationsBasedOnDestination.value = Event(R.id.masterChatRoomsFragment)
            }
            navigateToChatRooms()
        }
    }

    override fun onStart() {
        super.onStart()
        findNavController().addOnDestinationChangedListener(this)
    }

    override fun onStop() {
        findNavController().removeOnDestinationChangedListener(this)
        super.onStop()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (corePreferences.enableAnimations) {
            when (destination.id) {
                R.id.masterCallLogsFragment -> binding.motionLayout.transitionToState(R.id.call_history)
                R.id.masterContactsFragment -> binding.motionLayout.transitionToState(R.id.contacts)
                R.id.dialerFragment -> binding.motionLayout.transitionToState(R.id.dialer)
                R.id.masterChatRoomsFragment -> binding.motionLayout.transitionToState(R.id.chat_rooms)
            }
        } else {
            when (destination.id) {
                R.id.masterCallLogsFragment -> binding.motionLayout.setTransition(R.id.call_history, R.id.call_history)
                R.id.masterContactsFragment -> binding.motionLayout.setTransition(R.id.contacts, R.id.contacts)
                R.id.dialerFragment -> binding.motionLayout.setTransition(R.id.dialer, R.id.dialer)
                R.id.masterChatRoomsFragment -> binding.motionLayout.setTransition(R.id.chat_rooms, R.id.chat_rooms)
            }
        }
    }
}
