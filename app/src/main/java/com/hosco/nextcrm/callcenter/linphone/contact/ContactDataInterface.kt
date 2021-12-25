package org.linphone.contact

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.main.viewmodels.ErrorReportingViewModel
import org.linphone.core.Address
import org.linphone.core.ChatRoomSecurityLevel
import org.linphone.utils.LinphoneUtils

interface ContactDataInterface {
    val contact: MutableLiveData<Contact>

    val displayName: MutableLiveData<String>

    val securityLevel: MutableLiveData<ChatRoomSecurityLevel>

    val showGroupChatAvatar: Boolean
        get() = false
}

open class GenericContactData(private val sipAddress: Address) : ContactDataInterface {
    final override val contact: MutableLiveData<Contact> = MutableLiveData<Contact>()
    final override val displayName: MutableLiveData<String> = MutableLiveData<String>()
    final override val securityLevel: MutableLiveData<ChatRoomSecurityLevel> = MutableLiveData<ChatRoomSecurityLevel>()

    private val contactsUpdatedListener = object : ContactsUpdatedListenerStub() {
        override fun onContactUpdated(contact: Contact) {
            contactLookup()
        }
    }

    init {
        securityLevel.value = ChatRoomSecurityLevel.ClearText
        coreContext.contactsManager.addListener(contactsUpdatedListener)
        contactLookup()
    }

    open fun destroy() {
        coreContext.contactsManager.removeListener(contactsUpdatedListener)
    }

    private fun contactLookup() {
        displayName.value = LinphoneUtils.getDisplayName(sipAddress)
        contact.value =
            coreContext.contactsManager.findContactByAddress(sipAddress)
    }
}

abstract class GenericContactViewModel(private val sipAddress: Address) : ErrorReportingViewModel(), ContactDataInterface {
    final override val contact: MutableLiveData<Contact> = MutableLiveData<Contact>()
    final override val displayName: MutableLiveData<String> = MutableLiveData<String>()
    final override val securityLevel: MutableLiveData<ChatRoomSecurityLevel> = MutableLiveData<ChatRoomSecurityLevel>()

    private val contactsUpdatedListener = object : ContactsUpdatedListenerStub() {
        override fun onContactUpdated(contact: Contact) {
            contactLookup()
        }
    }

    init {
        securityLevel.value = ChatRoomSecurityLevel.ClearText
        coreContext.contactsManager.addListener(contactsUpdatedListener)
        contactLookup()
    }

    override fun onCleared() {
        coreContext.contactsManager.removeListener(contactsUpdatedListener)

        super.onCleared()
    }

    private fun contactLookup() {
        displayName.value = LinphoneUtils.getDisplayName(sipAddress)
        contact.value = coreContext.contactsManager.findContactByAddress(sipAddress)
    }
}
