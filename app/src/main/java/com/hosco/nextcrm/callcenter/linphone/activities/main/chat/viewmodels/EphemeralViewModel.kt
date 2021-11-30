package org.linphone.activities.main.chat.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import org.linphone.activities.main.chat.data.DurationItemClicked
import org.linphone.activities.main.chat.data.EphemeralDurationData
import org.linphone.core.ChatRoom
import org.linphone.core.tools.Log

class EphemeralViewModelFactory(private val chatRoom: ChatRoom) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EphemeralViewModel(chatRoom) as T
    }
}

class EphemeralViewModel(private val chatRoom: ChatRoom) : ViewModel() {
    val durationsList = MutableLiveData<ArrayList<EphemeralDurationData>>()

    var currentSelectedDuration: Long = 0

    private val listener = object : DurationItemClicked {
        override fun onDurationValueChanged(duration: Long) {
            currentSelectedDuration = duration
            computeEphemeralDurationValues()
        }
    }

    init {
        Log.i("[Ephemeral Messages] Current lifetime is ${chatRoom.ephemeralLifetime}, ephemeral enabled? ${chatRoom.ephemeralEnabled()}")
        currentSelectedDuration = if (chatRoom.ephemeralEnabled()) chatRoom.ephemeralLifetime else 0
        computeEphemeralDurationValues()
    }

    fun updateChatRoomEphemeralDuration() {
        Log.i("[Ephemeral Messages] Selected value is $currentSelectedDuration")
        if (currentSelectedDuration > 0) {
            if (chatRoom.ephemeralLifetime != currentSelectedDuration) {
                Log.i("[Ephemeral Messages] Setting new lifetime for ephemeral messages to $currentSelectedDuration")
                chatRoom.ephemeralLifetime = currentSelectedDuration
            } else {
                Log.i("[Ephemeral Messages] Configured lifetime for ephemeral messages was already $currentSelectedDuration")
            }

            if (!chatRoom.ephemeralEnabled()) {
                Log.i("[Ephemeral Messages] Ephemeral messages were disabled, enable them")
                chatRoom.enableEphemeral(true)
            }
        } else if (chatRoom.ephemeralEnabled()) {
            Log.i("[Ephemeral Messages] Ephemeral messages were enabled, disable them")
            chatRoom.enableEphemeral(false)
        }
    }

    private fun computeEphemeralDurationValues() {
        val list = arrayListOf<EphemeralDurationData>()
        list.add(EphemeralDurationData(R.string.chat_room_ephemeral_message_disabled, currentSelectedDuration, 0, listener))
        list.add(EphemeralDurationData(R.string.chat_room_ephemeral_message_one_minute, currentSelectedDuration, 60, listener))
        list.add(EphemeralDurationData(R.string.chat_room_ephemeral_message_one_hour, currentSelectedDuration, 3600, listener))
        list.add(EphemeralDurationData(R.string.chat_room_ephemeral_message_one_day, currentSelectedDuration, 86400, listener))
        list.add(EphemeralDurationData(R.string.chat_room_ephemeral_message_three_days, currentSelectedDuration, 259200, listener))
        list.add(EphemeralDurationData(R.string.chat_room_ephemeral_message_one_week, currentSelectedDuration, 604800, listener))
        durationsList.value = list
    }
}
