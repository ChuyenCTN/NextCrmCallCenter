package org.linphone.activities.main.contact.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ContactListCellBinding
import com.hosco.nextcrm.callcenter.databinding.GenericListHeaderBinding
import org.linphone.activities.main.adapters.SelectionListAdapter
import org.linphone.activities.main.contact.viewmodels.ContactViewModel
import org.linphone.activities.main.viewmodels.ListTopBarViewModel
import org.linphone.contact.Contact
import org.linphone.utils.AppUtils
import org.linphone.utils.Event
import org.linphone.utils.HeaderAdapter

class ContactsListAdapter(
    selectionVM: ListTopBarViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) : SelectionListAdapter<ContactViewModel, RecyclerView.ViewHolder>(selectionVM, ContactDiffCallback()), HeaderAdapter {
    val selectedContactEvent: MutableLiveData<Event<Contact>> by lazy {
        MutableLiveData<Event<Contact>>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ContactListCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.contact_list_cell, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position))
    }

    inner class ViewHolder(
        val binding: ContactListCellBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contactViewModel: ContactViewModel) {
            with(binding) {
                viewModel = contactViewModel

                lifecycleOwner = viewLifecycleOwner

                // This is for item selection through ListTopBarFragment
                selectionListViewModel = selectionViewModel
                selectionViewModel.isEditionEnabled.observe(
                    viewLifecycleOwner,
                    {
                        position = adapterPosition
                    }
                )

                setClickListener {
                    if (selectionViewModel.isEditionEnabled.value == true) {
                        selectionViewModel.onToggleSelect(adapterPosition)
                    } else {
                        selectedContactEvent.value = Event(contactViewModel.contactInternal)
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

    override fun displayHeaderForPosition(position: Int): Boolean {
        if (position >= itemCount) return false
        val contact = getItem(position)
        val firstLetter = contact.name.first().toString()
        val previousPosition = position - 1
        return if (previousPosition >= 0) {
            val previousItemFirstLetter = getItem(previousPosition).name.first().toString()
            previousItemFirstLetter != firstLetter
        } else true
    }

    override fun getHeaderViewForPosition(context: Context, position: Int): View {
        val contact = getItem(position)
        val firstLetter = AppUtils.getInitials(contact.name, 1)
        val binding: GenericListHeaderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.generic_list_header, null, false
        )
        binding.title = firstLetter
        binding.executePendingBindings()
        return binding.root
    }
}

private class ContactDiffCallback : DiffUtil.ItemCallback<ContactViewModel>() {
    override fun areItemsTheSame(
        oldItem: ContactViewModel,
        newItem: ContactViewModel
    ): Boolean {
        return oldItem.contactInternal.compareTo(newItem.contactInternal) == 0
    }

    override fun areContentsTheSame(
        oldItem: ContactViewModel,
        newItem: ContactViewModel
    ): Boolean {
        return false // For headers
    }
}
