package org.linphone.activities.main.chat.data

import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.contact.Contact
import org.linphone.contact.ContactDataInterface
import org.linphone.core.*
import org.linphone.utils.LinphoneUtils

class ChatRoomCreationContactData(private val searchResult: SearchResult) : ContactDataInterface {
    override val contact: MutableLiveData<Contact> = MutableLiveData<Contact>()
    override val displayName: MutableLiveData<String> = MutableLiveData<String>()
    override val securityLevel: MutableLiveData<ChatRoomSecurityLevel> = MutableLiveData<ChatRoomSecurityLevel>()

    val isDisabled: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSelected: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isLinphoneUser: Boolean by lazy {
        searchResult.friend?.getPresenceModelForUriOrTel(searchResult.phoneNumber ?: searchResult.address?.asStringUriOnly() ?: "")?.basicStatus == PresenceBasicStatus.Open
    }

    val sipUri: String by lazy {
        searchResult.phoneNumber ?: LinphoneUtils.getDisplayableAddress(searchResult.address)
    }

    val address: Address? by lazy {
        searchResult.address
    }

    val hasLimeX3DHCapability: Boolean
        get() = searchResult.hasCapability(FriendCapability.LimeX3Dh)

    init {
        isDisabled.value = false
        isSelected.value = false
        searchMatchingContact()
    }

    private fun searchMatchingContact() {
        val address = searchResult.address
        if (address != null) {
            contact.value = coreContext.contactsManager.findContactByAddress(address)
            displayName.value = searchResult.friend?.name ?: LinphoneUtils.getDisplayName(address)
        } else if (searchResult.phoneNumber != null) {
            contact.value = coreContext.contactsManager.findContactByPhoneNumber(searchResult.phoneNumber.orEmpty())
            displayName.value = searchResult.friend?.name ?: searchResult.phoneNumber.orEmpty()
        }
    }
}
