package org.linphone.activities.main.history.data

import org.linphone.activities.main.history.viewmodels.CallLogViewModel
import org.linphone.core.CallLog

class GroupedCallLogData(callLog: CallLog) {
    var lastCallLog: CallLog = callLog
    val callLogs = arrayListOf(callLog)
    val lastCallLogViewModel = CallLogViewModel(lastCallLog)

    fun destroy() {
        lastCallLogViewModel.destroy()
    }
}
