package org.linphone.activities.main.chat.data

import org.linphone.contact.GenericContactData
import org.linphone.core.ParticipantImdnState
import org.linphone.utils.TimestampUtils

class ImdnParticipantData(val imdnState: ParticipantImdnState) : GenericContactData(imdnState.participant.address) {
    val sipUri: String = imdnState.participant.address.asStringUriOnly()

    val time: String = TimestampUtils.toString(imdnState.stateChangeTime)

    override fun destroy() {
        super.destroy()
    }
}
