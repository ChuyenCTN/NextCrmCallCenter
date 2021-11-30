package org.linphone.activities.main.history.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.contact.GenericContactViewModel
import org.linphone.core.*
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.LinphoneUtils
import org.linphone.utils.TimestampUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.hosco.nextcrm.callcenter.R

class CallLogViewModelFactory(private val callLog: CallLog) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CallLogViewModel(callLog) as T
    }
}

class CallLogViewModel(val callLog: CallLog) : GenericContactViewModel(callLog.remoteAddress) {
    val peerSipUri: String by lazy {
        LinphoneUtils.getDisplayableAddress(callLog.remoteAddress)
    }

    val statusIconResource: Int by lazy {
        if (callLog.dir == Call.Dir.Incoming) {
            if (callLog.status == Call.Status.Missed) {
                R.drawable.call_status_missed
            } else {
                R.drawable.call_status_incoming
            }
        } else {
            R.drawable.call_status_outgoing
        }
    }

    val iconContentDescription: Int by lazy {
        if (callLog.dir == Call.Dir.Incoming) {
            if (callLog.status == Call.Status.Missed) {
                R.string.content_description_missed_call
            } else {
                R.string.content_description_incoming_call
            }
        } else {
            R.string.content_description_outgoing_call
        }
    }

    val directionIconResource: Int by lazy {
        if (callLog.dir == Call.Dir.Incoming) {
            if (callLog.status == Call.Status.Missed) {
                R.drawable.call_missed
            } else {
                R.drawable.call_incoming
            }
        } else {
            R.drawable.call_outgoing
        }
    }

    val duration: String by lazy {
        val dateFormat = SimpleDateFormat(if (callLog.duration >= 3600) "HH:mm:ss" else "mm:ss", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal[0, 0, 0, 0, 0] = callLog.duration
        dateFormat.format(cal.time)
    }

    val date: String by lazy {
        TimestampUtils.toString(callLog.startDate, shortDate = false, hideYear = false)
    }

    val startCallEvent: MutableLiveData<Event<CallLog>> by lazy {
        MutableLiveData<Event<CallLog>>()
    }

    val chatRoomCreatedEvent: MutableLiveData<Event<ChatRoom>> by lazy {
        MutableLiveData<Event<ChatRoom>>()
    }

    val waitForChatRoomCreation = MutableLiveData<Boolean>()

    val chatAllowed = !corePreferences.disableChat

    val secureChatAllowed = contact.value?.friend?.getPresenceModelForUriOrTel(peerSipUri)?.hasCapability(FriendCapability.LimeX3Dh) ?: false

    val relatedCallLogs = MutableLiveData<ArrayList<CallLog>>()

    private val chatRoomListener = object : ChatRoomListenerStub() {
        override fun onStateChanged(chatRoom: ChatRoom, state: ChatRoom.State) {
            if (state == ChatRoom.State.Created) {
                waitForChatRoomCreation.value = false
                chatRoomCreatedEvent.value = Event(chatRoom)
            } else if (state == ChatRoom.State.CreationFailed) {
                Log.e("[History Detail] Group chat room creation has failed !")
                waitForChatRoomCreation.value = false
                onErrorEvent.value = Event(R.string.chat_room_creation_failed_snack)
            }
        }
    }

    init {
        waitForChatRoomCreation.value = false
    }

    override fun onCleared() {
        destroy()
        super.onCleared()
    }

    fun destroy() {
    }

    fun startCall() {
        startCallEvent.value = Event(callLog)
    }

    fun startChat(isSecured: Boolean) {
        waitForChatRoomCreation.value = true
        val chatRoom = LinphoneUtils.createOneToOneChatRoom(callLog.remoteAddress, isSecured)
        if (chatRoom != null) {
            if (chatRoom.state == ChatRoom.State.Created) {
                waitForChatRoomCreation.value = false
                chatRoomCreatedEvent.value = Event(chatRoom)
            } else {
                chatRoom.addListener(chatRoomListener)
            }
        } else {
            waitForChatRoomCreation.value = false
            Log.e("[History Detail] Couldn't create chat room with address ${callLog.remoteAddress}")
            onErrorEvent.value = Event(R.string.chat_room_creation_failed_snack)
        }
    }

    fun getCallsHistory(): ArrayList<CallLogViewModel> {
        val callsHistory = ArrayList<CallLogViewModel>()
        for (callLog in relatedCallLogs.value.orEmpty()) {
            callsHistory.add(CallLogViewModel(callLog))
        }
        return callsHistory
    }
}
