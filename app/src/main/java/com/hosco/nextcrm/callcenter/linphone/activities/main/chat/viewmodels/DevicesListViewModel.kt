package org.linphone.activities.main.chat.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.linphone.activities.main.chat.data.DevicesListGroupData
import org.linphone.core.ChatRoom
import org.linphone.core.ChatRoomListenerStub
import org.linphone.core.EventLog

class DevicesListViewModelFactory(private val chatRoom: ChatRoom) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DevicesListViewModel(chatRoom) as T
    }
}

class DevicesListViewModel(private val chatRoom: ChatRoom) : ViewModel() {
    val participants = MutableLiveData<ArrayList<DevicesListGroupData>>()

    private val listener = object : ChatRoomListenerStub() {
        override fun onParticipantDeviceAdded(chatRoom: ChatRoom, eventLog: EventLog) {
            updateParticipants()
        }

        override fun onParticipantDeviceRemoved(chatRoom: ChatRoom, eventLog: EventLog) {
            updateParticipants()
        }

        override fun onParticipantAdded(chatRoom: ChatRoom, eventLog: EventLog) {
            updateParticipants()
        }

        override fun onParticipantRemoved(chatRoom: ChatRoom, eventLog: EventLog) {
            updateParticipants()
        }
    }

    init {
        chatRoom.addListener(listener)
        updateParticipants()
    }

    override fun onCleared() {
        participants.value.orEmpty().forEach(DevicesListGroupData::destroy)
        chatRoom.removeListener(listener)
        super.onCleared()
    }

    private fun updateParticipants() {
        participants.value.orEmpty().forEach(DevicesListGroupData::destroy)

        val list = arrayListOf<DevicesListGroupData>()
        val me = chatRoom.me
        if (me != null) list.add(DevicesListGroupData(me))
        for (participant in chatRoom.participants) {
            list.add(DevicesListGroupData(participant))
        }

        participants.value = list
    }
}
