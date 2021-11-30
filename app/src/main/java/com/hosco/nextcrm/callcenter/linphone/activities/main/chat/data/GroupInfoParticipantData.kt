package org.linphone.activities.main.chat.data

import androidx.lifecycle.MutableLiveData
import org.linphone.activities.main.chat.GroupChatRoomMember
import org.linphone.contact.GenericContactData
import org.linphone.utils.LinphoneUtils

class GroupInfoParticipantData(val participant: GroupChatRoomMember) : GenericContactData(participant.address) {
    val sipUri: String get() = LinphoneUtils.getDisplayableAddress(participant.address)

    val isAdmin = MutableLiveData<Boolean>()

    val showAdminControls = MutableLiveData<Boolean>()

    // A participant not yet added to a group can't be set admin at the same time it's added
    val canBeSetAdmin = MutableLiveData<Boolean>()

    init {
        securityLevel.value = participant.securityLevel
        isAdmin.value = participant.isAdmin
        showAdminControls.value = false
        canBeSetAdmin.value = participant.canBeSetAdmin
    }

    override fun destroy() {
        super.destroy()
    }

    fun setAdmin() {
        isAdmin.value = true
        participant.isAdmin = true
    }

    fun unSetAdmin() {
        isAdmin.value = false
        participant.isAdmin = false
    }
}
