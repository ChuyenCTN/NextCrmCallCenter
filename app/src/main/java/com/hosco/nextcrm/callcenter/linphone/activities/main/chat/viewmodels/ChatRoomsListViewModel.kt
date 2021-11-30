package org.linphone.activities.main.chat.viewmodels

import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.main.viewmodels.ErrorReportingViewModel
import org.linphone.compatibility.Compatibility
import org.linphone.contact.ContactsUpdatedListenerStub
import org.linphone.core.*
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.LinphoneUtils
import com.hosco.nextcrm.callcenter.R

class ChatRoomsListViewModel : ErrorReportingViewModel() {
    val chatRooms = MutableLiveData<ArrayList<ChatRoomViewModel>>()

    val contactsUpdatedEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    val fileSharingPending = MutableLiveData<Boolean>()

    val textSharingPending = MutableLiveData<Boolean>()

    val forwardPending = MutableLiveData<Boolean>()

    val groupChatAvailable: Boolean = LinphoneUtils.isGroupChatAvailable()

    private val contactsUpdatedListener = object : ContactsUpdatedListenerStub() {
        override fun onContactsUpdated() {
            Log.i("[Chat Rooms] Contacts have changed")
            contactsUpdatedEvent.value = Event(true)
        }
    }

    private val listener: CoreListenerStub = object : CoreListenerStub() {
        override fun onChatRoomStateChanged(core: Core, chatRoom: ChatRoom, state: ChatRoom.State) {
            if (state == ChatRoom.State.Created) {
                if (chatRoom in core.chatRooms) { // Don't add empty chat room if 1-1 depending on policy
                    addChatRoom(chatRoom)
                }
            } else if (state == ChatRoom.State.TerminationFailed) {
                Log.e("[Chat Rooms] Group chat room removal for address ${chatRoom.peerAddress.asStringUriOnly()} has failed !")
                onErrorEvent.value = Event(R.string.chat_room_removal_failed_snack)
            }
        }

        override fun onMessageSent(core: Core, chatRoom: ChatRoom, message: ChatMessage) {
            when (findChatRoomIndex(chatRoom)) {
                -1 -> addChatRoom(chatRoom)
                0 -> {}
                else -> reorderChatRooms()
            }
        }

        override fun onMessageReceived(core: Core, chatRoom: ChatRoom, message: ChatMessage) {
            when (findChatRoomIndex(chatRoom)) {
                -1 -> addChatRoom(chatRoom)
                0 -> {}
                else -> reorderChatRooms()
            }
        }
    }

    private val chatRoomListener = object : ChatRoomListenerStub() {
        override fun onStateChanged(chatRoom: ChatRoom, newState: ChatRoom.State) {
            if (newState == ChatRoom.State.Deleted) {
                val list = arrayListOf<ChatRoomViewModel>()
                for (chatRoomViewModel in chatRooms.value.orEmpty()) {
                    if (chatRoomViewModel.chatRoom != chatRoom) {
                        list.add(chatRoomViewModel)
                    } else {
                        chatRoomViewModel.destroy()
                    }
                }
                chatRooms.value = list
            }
        }
    }

    private var chatRoomsToDeleteCount = 0

    init {
        updateChatRooms()
        coreContext.core.addListener(listener)
        coreContext.contactsManager.addListener(contactsUpdatedListener)
    }

    override fun onCleared() {
        chatRooms.value.orEmpty().forEach(ChatRoomViewModel::destroy)
        coreContext.contactsManager.removeListener(contactsUpdatedListener)
        coreContext.core.removeListener(listener)

        super.onCleared()
    }

    fun deleteChatRoom(chatRoom: ChatRoom?) {
        for (eventLog in chatRoom?.getHistoryMessageEvents(0).orEmpty()) {
            LinphoneUtils.deleteFilesAttachedToEventLog(eventLog)
        }

        chatRoomsToDeleteCount = 1
        if (chatRoom != null) {
            coreContext.notificationsManager.dismissChatNotification(chatRoom)
            Compatibility.removeChatRoomShortcut(coreContext.context, chatRoom)
            chatRoom.addListener(chatRoomListener)
            coreContext.core.deleteChatRoom(chatRoom)
        }
    }

    fun deleteChatRooms(chatRooms: ArrayList<ChatRoom>) {
        chatRoomsToDeleteCount = chatRooms.size
        for (chatRoom in chatRooms) {
            for (eventLog in chatRoom.getHistoryMessageEvents(0)) {
                LinphoneUtils.deleteFilesAttachedToEventLog(eventLog)
            }

            coreContext.notificationsManager.dismissChatNotification(chatRoom)
            Compatibility.removeChatRoomShortcut(coreContext.context, chatRoom)
            chatRoom.addListener(chatRoomListener)
            chatRoom.core.deleteChatRoom(chatRoom)
        }
    }

    private fun updateChatRooms() {
        for (chatRoomViewModel in chatRooms.value.orEmpty()) {
            chatRoomViewModel.destroy()
        }

        val list = arrayListOf<ChatRoomViewModel>()
        for (chatRoom in coreContext.core.chatRooms) {
            val viewModel = ChatRoomViewModel(chatRoom)
            list.add(viewModel)
        }
        chatRooms.value = list
    }

    private fun addChatRoom(chatRoom: ChatRoom) {
        val list = arrayListOf<ChatRoomViewModel>()
        val viewModel = ChatRoomViewModel(chatRoom)
        list.add(viewModel)
        list.addAll(chatRooms.value.orEmpty())
        chatRooms.value = list
    }

    private fun reorderChatRooms() {
        val list = arrayListOf<ChatRoomViewModel>()
        list.addAll(chatRooms.value.orEmpty())
        list.sortByDescending { chatRoomViewModel -> chatRoomViewModel.chatRoom.lastUpdateTime }
        chatRooms.value = list
    }

    private fun findChatRoomIndex(chatRoom: ChatRoom): Int {
        var index = 0
        for (chatRoomViewModel in chatRooms.value.orEmpty()) {
            if (chatRoomViewModel.chatRoom == chatRoom) {
                return index
            }
            index++
        }
        return -1
    }
}
