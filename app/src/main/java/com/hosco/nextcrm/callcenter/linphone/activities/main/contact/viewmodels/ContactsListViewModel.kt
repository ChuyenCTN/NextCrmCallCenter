package org.linphone.activities.main.contact.viewmodels

import android.content.ContentProviderOperation
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.contact.Contact
import org.linphone.contact.ContactsUpdatedListenerStub
import org.linphone.contact.NativeContact
import org.linphone.core.tools.Log

class ContactsListViewModel : ViewModel() {
    val sipContactsSelected = MutableLiveData<Boolean>()

    val contactsList = MutableLiveData<ArrayList<ContactViewModel>>()

    val filter = MutableLiveData<String>()

    private val contactsUpdatedListener = object : ContactsUpdatedListenerStub() {
        override fun onContactsUpdated() {
            Log.i("[Contacts] Contacts have changed")
            updateContactsList()
        }
    }

    init {
        sipContactsSelected.value = coreContext.contactsManager.shouldDisplaySipContactsList()

        coreContext.contactsManager.addListener(contactsUpdatedListener)
    }

    override fun onCleared() {
        contactsList.value.orEmpty().forEach(ContactViewModel::destroy)
        coreContext.contactsManager.removeListener(contactsUpdatedListener)

        super.onCleared()
    }

    private fun getSelectedContactsList(): ArrayList<ContactViewModel> {
        val list = arrayListOf<ContactViewModel>()
        val source =
            if (sipContactsSelected.value == true) coreContext.contactsManager.sipContacts
            else coreContext.contactsManager.contacts
        for (contact in source) {
            list.add(ContactViewModel(contact))
        }
        return list
    }

    fun updateContactsList() {
        val list: ArrayList<ContactViewModel>

        val filterValue = filter.value.orEmpty()
        list = if (filterValue.isNotEmpty()) {
            getSelectedContactsList().filter { contact ->
                contact.name.contains(filterValue, true)
            } as ArrayList<ContactViewModel>
        } else {
            getSelectedContactsList()
        }

        contactsList.postValue(list)
    }

    fun deleteContact(contact: Contact?) {
        contact ?: return

        val select = ContactsContract.Data.CONTACT_ID + " = ?"
        val ops = ArrayList<ContentProviderOperation>()

        if (contact is NativeContact) {
            val nativeContact: NativeContact = contact
            Log.i("[Contacts] Adding Android contact id ${nativeContact.nativeId} to batch removal")
            val args = arrayOf(nativeContact.nativeId)
            ops.add(
                ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(select, args)
                    .build()
            )
        }

        if (contact.friend != null) {
            Log.i("[Contacts] Removing friend")
            contact.friend?.remove()
        }

        if (ops.isNotEmpty()) {
            try {
                Log.i("[Contacts] Removing ${ops.size} contacts")
                coreContext.context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            } catch (e: Exception) {
                Log.e("[Contacts] $e")
            }
        }
    }

    fun deleteContacts(list: ArrayList<Contact>) {
        val select = ContactsContract.Data.CONTACT_ID + " = ?"
        val ops = ArrayList<ContentProviderOperation>()

        for (contact in list) {
            if (contact is NativeContact) {
                val nativeContact: NativeContact = contact
                Log.i("[Contacts] Adding Android contact id ${nativeContact.nativeId} to batch removal")
                val args = arrayOf(nativeContact.nativeId)
                ops.add(
                    ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                        .withSelection(select, args)
                        .build()
                )
            }

            if (contact.friend != null) {
                Log.i("[Contacts] Removing friend")
                contact.friend?.remove()
            }
        }

        if (ops.isNotEmpty()) {
            try {
                Log.i("[Contacts] Removing ${ops.size} contacts")
                coreContext.context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            } catch (e: Exception) {
                Log.e("[Contacts] $e")
            }
        }
    }
}
