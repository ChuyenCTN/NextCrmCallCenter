package org.linphone.activities.main.chat.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ChatRoomDevicesFragmentBinding
import org.linphone.activities.main.chat.viewmodels.DevicesListViewModel
import org.linphone.activities.main.chat.viewmodels.DevicesListViewModelFactory
import org.linphone.activities.main.fragments.SecureFragment
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log

class DevicesFragment : SecureFragment<ChatRoomDevicesFragmentBinding>() {
    private lateinit var listViewModel: DevicesListViewModel
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int = R.layout.chat_room_devices_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        val chatRoom = sharedViewModel.selectedChatRoom.value
        if (chatRoom == null) {
            Log.e("[Devices] Chat room is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        isSecure = chatRoom.currentParams.encryptionEnabled()

        listViewModel = ViewModelProvider(
            this,
            DevicesListViewModelFactory(chatRoom)
        )[DevicesListViewModel::class.java]
        binding.viewModel = listViewModel

        binding.setBackClickListener {
            goBack()
        }
    }
}
