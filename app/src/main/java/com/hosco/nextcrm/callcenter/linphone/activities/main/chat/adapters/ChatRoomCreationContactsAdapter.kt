package org.linphone.activities.main.chat.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ChatRoomCreationContactCellBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.main.chat.data.ChatRoomCreationContactData
import org.linphone.core.Address
import org.linphone.core.FriendCapability
import org.linphone.core.SearchResult
import org.linphone.utils.Event

class ChatRoomCreationContactsAdapter(
    private val viewLifecycleOwner: LifecycleOwner
) : ListAdapter<SearchResult, RecyclerView.ViewHolder>(SearchResultDiffCallback()) {
    val selectedContact = MutableLiveData<Event<SearchResult>>()

    var groupChatEnabled: Boolean = false

    private var selectedAddresses = ArrayList<Address>()

    private var securityEnabled: Boolean = false

    fun updateSelectedAddresses(selection: ArrayList<Address>) {
        selectedAddresses = selection
        notifyDataSetChanged()
    }

    fun updateSecurity(enabled: Boolean) {
        securityEnabled = enabled
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ChatRoomCreationContactCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.chat_room_creation_contact_cell, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ChatRoomCreationContactCellBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(searchResult: SearchResult) {
            with(binding) {
                val searchResultViewModel = ChatRoomCreationContactData(searchResult)
                data = searchResultViewModel

                lifecycleOwner = viewLifecycleOwner

                updateSecurity(searchResult, searchResultViewModel, securityEnabled)

                val selected = selectedAddresses.find { address ->
                    val searchAddress = searchResult.address
                    if (searchAddress != null) address.weakEqual(searchAddress) else false
                }
                searchResultViewModel.isSelected.value = selected != null

                setClickListener {
                    selectedContact.value = Event(searchResult)
                }

                executePendingBindings()
            }
        }

        private fun updateSecurity(
            searchResult: SearchResult,
            viewModel: ChatRoomCreationContactData,
            securityEnabled: Boolean
        ) {
            val searchAddress = searchResult.address
            val isMyself = securityEnabled && searchAddress != null && coreContext.core.defaultAccount?.params?.identityAddress?.weakEqual(searchAddress) ?: false
            val limeCheck = !securityEnabled || (securityEnabled && searchResult.hasCapability(FriendCapability.LimeX3Dh))
            val groupCheck = !groupChatEnabled || (groupChatEnabled && searchResult.hasCapability(FriendCapability.GroupChat))
            val disabled = if (searchResult.friend != null) !limeCheck || !groupCheck || isMyself else false // Generated entry from search filter

            viewModel.isDisabled.value = disabled

            if (disabled && viewModel.isSelected.value == true) {
                // Remove item from selection if both selected and disabled
                selectedContact.postValue(Event(searchResult))
            }
        }
    }
}

private class SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResult>() {
    override fun areItemsTheSame(
        oldItem: SearchResult,
        newItem: SearchResult
    ): Boolean {
        val oldAddress = oldItem.address
        val newAddress = newItem.address
        return if (oldAddress != null && newAddress != null) oldAddress.weakEqual(newAddress) else false
    }

    override fun areContentsTheSame(
        oldItem: SearchResult,
        newItem: SearchResult
    ): Boolean {
        return newItem.friend != null
    }
}
