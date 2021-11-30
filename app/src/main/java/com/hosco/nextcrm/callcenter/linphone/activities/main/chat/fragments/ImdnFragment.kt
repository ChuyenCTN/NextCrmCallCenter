package org.linphone.activities.main.chat.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ChatRoomImdnFragmentBinding
import org.linphone.activities.main.chat.adapters.ImdnAdapter
import org.linphone.activities.main.chat.viewmodels.ImdnViewModel
import org.linphone.activities.main.chat.viewmodels.ImdnViewModelFactory
import org.linphone.activities.main.fragments.SecureFragment
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.AppUtils
import org.linphone.utils.RecyclerViewHeaderDecoration

class ImdnFragment : SecureFragment<ChatRoomImdnFragmentBinding>() {
    private lateinit var viewModel: ImdnViewModel
    private lateinit var adapter: ImdnAdapter
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int {
        return R.layout.chat_room_imdn_fragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        val chatRoom = sharedViewModel.selectedChatRoom.value
        if (chatRoom == null) {
            Log.e("[IMDN] Chat room is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        isSecure = chatRoom.currentParams.encryptionEnabled()

        if (arguments != null) {
            val messageId = arguments?.getString("MessageId")
            val message = if (messageId != null) chatRoom.findMessage(messageId) else null
            if (message != null) {
                Log.i("[IMDN] Found message $message with id $messageId")
                viewModel = ViewModelProvider(
                    this,
                    ImdnViewModelFactory(message)
                )[ImdnViewModel::class.java]
                binding.viewModel = viewModel
            } else {
                Log.e("[IMDN] Couldn't find message with id $messageId in chat room $chatRoom")
                findNavController().popBackStack()
                return
            }
        } else {
            Log.e("[IMDN] Couldn't find message id in intent arguments")
            findNavController().popBackStack()
            return
        }

        adapter = ImdnAdapter(viewLifecycleOwner)
        binding.participantsList.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        binding.participantsList.layoutManager = layoutManager

        // Divider between items
        binding.participantsList.addItemDecoration(AppUtils.getDividerDecoration(requireContext(), layoutManager))

        // Displays state header
        val headerItemDecoration = RecyclerViewHeaderDecoration(requireContext(), adapter)
        binding.participantsList.addItemDecoration(headerItemDecoration)

        viewModel.participants.observe(
            viewLifecycleOwner,
            {
                adapter.submitList(it)
            }
        )

        binding.setBackClickListener {
            goBack()
        }
    }
}
