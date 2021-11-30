package org.linphone.activities.main.chat.data

import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.contact.GenericContactData
import org.linphone.core.ChatRoomSecurityLevel
import org.linphone.core.Participant
import org.linphone.utils.LinphoneUtils

class DevicesListGroupData(private val participant: Participant) : GenericContactData(participant.address) {
    private val device = if (participant.devices.isEmpty()) null else participant.devices.first()

    val securityLevelIcon: Int by lazy {
        when (device?.securityLevel) {
            ChatRoomSecurityLevel.Safe -> R.drawable.security_2_indicator
            ChatRoomSecurityLevel.Encrypted -> R.drawable.security_1_indicator
            else -> R.drawable.security_alert_indicator
        }
    }

    val securityLevelContentDescription: Int by lazy {
        when (device?.securityLevel) {
            ChatRoomSecurityLevel.Safe -> R.string.content_description_security_level_safe
            ChatRoomSecurityLevel.Encrypted -> R.string.content_description_security_level_encrypted
            else -> R.string.content_description_security_level_unsafe
        }
    }

    val sipUri: String get() = LinphoneUtils.getDisplayableAddress(participant.address)

    val isExpanded = MutableLiveData<Boolean>()

    val devices = MutableLiveData<ArrayList<DevicesListChildData>>()

    init {
        securityLevel.value = participant.securityLevel
        isExpanded.value = false

        val list = arrayListOf<DevicesListChildData>()
        for (device in participant.devices) {
            list.add(DevicesListChildData((device)))
        }
        devices.value = list
    }

    fun toggleExpanded() {
        isExpanded.value = isExpanded.value != true
    }

    fun onClick() {
        if (device?.address != null) coreContext.startCall(device.address, true)
    }
}
