package org.linphone.activities.main.chat.viewmodels

import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.main.viewmodels.ErrorReportingViewModel
import org.linphone.contact.ContactsUpdatedListenerStub
import org.linphone.core.*
import org.linphone.core.tools.Log
import org.linphone.utils.AppUtils
import org.linphone.utils.Event
import org.linphone.utils.LinphoneUtils
import com.hosco.nextcrm.callcenter.R

class ChatRoomCreationViewModel : ErrorReportingViewModel() {
    val chatRoomCreatedEvent: MutableLiveData<Event<ChatRoom>> by lazy {
        MutableLiveData<Event<ChatRoom>>()
    }

    val createGroupChat = MutableLiveData<Boolean>()

    val sipContactsSelected = MutableLiveData<Boolean>()

    val isEncrypted = MutableLiveData<Boolean>()

    val contactsList = MutableLiveData<ArrayList<SearchResult>>()

    val waitForChatRoomCreation = MutableLiveData<Boolean>()

    val selectedAddresses = MutableLiveData<ArrayList<Address>>()

    val filter = MutableLiveData<String>()
    private var previousFilter = ""

    val limeAvailable: Boolean = LinphoneUtils.isLimeAvailable()

    private val contactsUpdatedListener = object : ContactsUpdatedListenerStub() {
        override fun onContactsUpdated() {
            Log.i("[Chat Room Creation] Contacts have changed")
            updateContactsList()
        }
    }

    private val listener = object : ChatRoomListenerStub() {
        override fun onStateChanged(room: ChatRoom, state: ChatRoom.State) {
            if (state == ChatRoom.State.Created) {
                waitForChatRoomCreation.value = false
                Log.i("[Chat Room Creation] Chat room created")
                chatRoomCreatedEvent.value = Event(room)
            } else if (state == ChatRoom.State.CreationFailed) {
                Log.e("[Chat Room Creation] Group chat room creation has failed !")
                waitForChatRoomCreation.value = false
                onErrorEvent.value = Event(R.string.chat_room_creation_failed_snack)
            }
        }
    }

    init {
        createGroupChat.value = false
        sipContactsSelected.value = coreContext.contactsManager.shouldDisplaySipContactsList()
        isEncrypted.value = false

        selectedAddresses.value = arrayListOf()

        coreContext.contactsManager.addListener(contactsUpdatedListener)
        waitForChatRoomCreation.value = false
    }

    override fun onCleared() {
        coreContext.contactsManager.removeListener(contactsUpdatedListener)

        super.onCleared()
    }

    fun updateEncryption(encrypted: Boolean) {
        isEncrypted.value = encrypted
    }

    fun applyFilter() {
        val filterValue = filter.value.orEmpty()
        if (previousFilter == filterValue) return

        if (previousFilter.isNotEmpty() && previousFilter.length > filterValue.length) {
            coreContext.contactsManager.magicSearch.resetSearchCache()
        }
        previousFilter = filterValue

        updateContactsList()
    }

    fun updateContactsList() {
        val domain = if (sipContactsSelected.value == true) coreContext.core.defaultAccount?.params?.domain ?: "" else ""
        val results = coreContext.contactsManager.magicSearch.getContactListFromFilter(filter.value.orEmpty(), domain)

        val list = arrayListOf<SearchResult>()
        for (result in results) {
            list.add(result)
        }
        contactsList.value = list
    }

    fun toggleSelectionForSearchResult(searchResult: SearchResult) {
        val address = searchResult.address
        if (address != null) {
            toggleSelectionForAddress(address)
        }
    }

    fun toggleSelectionForAddress(address: Address) {
        val list = arrayListOf<Address>()
        list.addAll(selectedAddresses.value.orEmpty())

        val found = list.find {
            it.weakEqual(address)
        }

        if (found != null) {
            list.remove(found)
        } else {
            val contact = coreContext.contactsManager.findContactByAddress(address)
            if (contact != null) address.displayName = contact.fullName
            list.add(address)
        }

        selectedAddresses.value = list
    }

    fun createOneToOneChat(searchResult: SearchResult) {
        waitForChatRoomCreation.value = true
        val defaultAccount = coreContext.core.defaultAccount
        var room: ChatRoom?

        val address = searchResult.address ?: coreContext.core.interpretUrl(searchResult.phoneNumber ?: "")
        if (address == null) {
            Log.e("[Chat Room Creation] Can't get a valid address from search result $searchResult")
            onErrorEvent.value = Event(R.string.chat_room_creation_failed_snack)
            waitForChatRoomCreation.value = false
            return
        }

        val encrypted = isEncrypted.value == true
        val params: ChatRoomParams = coreContext.core.createDefaultChatRoomParams()
        params.backend = ChatRoomBackend.Basic
        params.enableGroup(false)
        if (encrypted) {
            params.enableEncryption(true)
            params.backend = ChatRoomBackend.FlexisipChat
            params.ephemeralMode = if (corePreferences.useEphemeralPerDeviceMode)
                ChatRoomEphemeralMode.DeviceManaged
            else
                ChatRoomEphemeralMode.AdminManaged
            params.ephemeralLifetime = 0 // Make sure ephemeral is disabled by default
            Log.i("[Chat Room Creation] Ephemeral mode is ${params.ephemeralMode}, lifetime is ${params.ephemeralLifetime}")
            params.subject = AppUtils.getString(R.string.chat_room_dummy_subject)
        }

        val participants = arrayOf(address)
        val localAddress: Address? = defaultAccount?.params?.identityAddress

        room = coreContext.core.searchChatRoom(params, localAddress, null, participants)
        if (room == null) {
            Log.w("[Chat Room Creation] Couldn't find existing 1-1 chat room with remote ${address.asStringUriOnly()}, encryption=$encrypted and local identity ${localAddress?.asStringUriOnly()}")
            room = coreContext.core.createChatRoom(params, localAddress, participants)

            if (encrypted) {
                room?.addListener(listener)
            } else {
                if (room != null) {
                    chatRoomCreatedEvent.value = Event(room)
                } else {
                    Log.e("[Chat Room Creation] Couldn't create chat room with remote ${address.asStringUriOnly()} and local identity ${localAddress?.asStringUriOnly()}")
                }
                waitForChatRoomCreation.value = false
            }
        } else {
            Log.i("[Chat Room Creation] Found existing 1-1 chat room with remote ${address.asStringUriOnly()}, encryption=$encrypted and local identity ${localAddress?.asStringUriOnly()}")
            chatRoomCreatedEvent.value = Event(room)
            waitForChatRoomCreation.value = false
        }
    }
}
