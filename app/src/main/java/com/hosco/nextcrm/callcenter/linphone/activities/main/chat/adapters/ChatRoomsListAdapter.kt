package org.linphone.activities.main.chat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ChatRoomListCellBinding
import org.linphone.activities.main.adapters.SelectionListAdapter
import org.linphone.activities.main.chat.viewmodels.ChatRoomViewModel
import org.linphone.activities.main.viewmodels.ListTopBarViewModel
import org.linphone.core.ChatRoom
import org.linphone.utils.Event

class ChatRoomsListAdapter(
    selectionVM: ListTopBarViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) : SelectionListAdapter<ChatRoomViewModel, RecyclerView.ViewHolder>(selectionVM, ChatRoomDiffCallback()) {
    val selectedChatRoomEvent: MutableLiveData<Event<ChatRoom>> by lazy {
        MutableLiveData<Event<ChatRoom>>()
    }

    private var isForwardPending = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ChatRoomListCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chat_room_list_cell, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }

    fun forwardPending(pending: Boolean) {
        isForwardPending = pending
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ChatRoomListCellBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRoomViewModel: ChatRoomViewModel) {
            with(binding) {
                viewModel = chatRoomViewModel

                lifecycleOwner = viewLifecycleOwner

                // This is for item selection through ListTopBarFragment
                selectionListViewModel = selectionViewModel
                selectionViewModel.isEditionEnabled.observe(
                    viewLifecycleOwner,
                    {
                        position = adapterPosition
                    }
                )

                forwardPending = isForwardPending

                setClickListener {
                    if (selectionViewModel.isEditionEnabled.value == true) {
                        selectionViewModel.onToggleSelect(adapterPosition)
                    } else {
                        selectedChatRoomEvent.value = Event(chatRoomViewModel.chatRoom)
                    }
                }

                setLongClickListener {
                    if (selectionViewModel.isEditionEnabled.value == false) {
                        selectionViewModel.isEditionEnabled.value = true
                        // Selection will be handled by click listener
                        true
                    }
                    false
                }

                executePendingBindings()
            }
        }
    }
}

private class ChatRoomDiffCallback : DiffUtil.ItemCallback<ChatRoomViewModel>() {
    override fun areItemsTheSame(
        oldItem: ChatRoomViewModel,
        newItem: ChatRoomViewModel
    ): Boolean {
        return oldItem.chatRoom == newItem.chatRoom
    }

    override fun areContentsTheSame(
        oldItem: ChatRoomViewModel,
        newItem: ChatRoomViewModel
    ): Boolean {
        return newItem.unreadMessagesCount.value == 0
    }
}
