package org.linphone.activities.main.chat.data

import org.linphone.contact.GenericContactData
import org.linphone.core.EventLog

class EventLogData(val eventLog: EventLog) {
    val data: GenericContactData = if (eventLog.type == EventLog.Type.ConferenceChatMessage) {
        ChatMessageData(eventLog.chatMessage!!)
    } else {
        EventData(eventLog)
    }

    fun destroy() {
        data.destroy()
    }
}
