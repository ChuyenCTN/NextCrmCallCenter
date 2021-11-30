package org.linphone.activities.main.chat.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ChatRoomEphemeralFragmentBinding
import org.linphone.activities.main.chat.viewmodels.EphemeralViewModel
import org.linphone.activities.main.chat.viewmodels.EphemeralViewModelFactory
import org.linphone.activities.main.fragments.SecureFragment
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log

class EphemeralFragment : SecureFragment<ChatRoomEphemeralFragmentBinding>() {
    private lateinit var viewModel: EphemeralViewModel
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int {
        return R.layout.chat_room_ephemeral_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isSecure = true
        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        val chatRoom = sharedViewModel.selectedChatRoom.value
        if (chatRoom == null) {
            Log.e("[Ephemeral] Chat room is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        viewModel = ViewModelProvider(
            this,
            EphemeralViewModelFactory(chatRoom)
        )[EphemeralViewModel::class.java]
        binding.viewModel = viewModel

        binding.setBackClickListener {
            goBack()
        }

        binding.setValidClickListener {
            viewModel.updateChatRoomEphemeralDuration()
            goBack()
        }
    }
}
