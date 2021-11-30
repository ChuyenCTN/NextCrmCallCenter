package org.linphone.activities.main.history.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.main.history.data.GroupedCallLogData
import org.linphone.contact.ContactsUpdatedListenerStub
import org.linphone.core.*
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.LinphoneUtils
import org.linphone.utils.TimestampUtils

class CallLogsListViewModel : ViewModel() {
    val callLogs = MutableLiveData<ArrayList<GroupedCallLogData>>()
    val missedCallLogs = MutableLiveData<ArrayList<GroupedCallLogData>>()

    val missedCallLogsSelected = MutableLiveData<Boolean>()

    val contactsUpdatedEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    private val listener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State,
            message: String
        ) {
            updateCallLogs()
        }
    }

    private val contactsUpdatedListener = object : ContactsUpdatedListenerStub() {
        override fun onContactsUpdated() {
            Log.i("[Call Logs] Contacts have changed")
            contactsUpdatedEvent.value = Event(true)
        }
    }

    init {
        missedCallLogsSelected.value = false
        updateCallLogs()

        coreContext.core.addListener(listener)
        coreContext.contactsManager.addListener(contactsUpdatedListener)
    }

    override fun onCleared() {
        callLogs.value.orEmpty().forEach(GroupedCallLogData::destroy)
        missedCallLogs.value.orEmpty().forEach(GroupedCallLogData::destroy)

        coreContext.contactsManager.removeListener(contactsUpdatedListener)
        coreContext.core.removeListener(listener)

        super.onCleared()
    }

    fun deleteCallLogGroup(callLog: GroupedCallLogData?) {
        if (callLog != null) {
            for (log in callLog.callLogs) {
                coreContext.core.removeCallLog(log)
            }
        }

        updateCallLogs()
    }

    fun deleteCallLogGroups(listToDelete: ArrayList<GroupedCallLogData>) {
        for (callLog in listToDelete) {
            for (log in callLog.callLogs) {
                coreContext.core.removeCallLog(log)
            }
        }

        updateCallLogs()
    }

    private fun updateCallLogs() {
        callLogs.value.orEmpty().forEach(GroupedCallLogData::destroy)
        missedCallLogs.value.orEmpty().forEach(GroupedCallLogData::destroy)

        val list = arrayListOf<GroupedCallLogData>()
        val missedList = arrayListOf<GroupedCallLogData>()

        var previousCallLogGroup: GroupedCallLogData? = null
        var previousMissedCallLogGroup: GroupedCallLogData? = null
        for (callLog in coreContext.core.callLogs) {
            if (previousCallLogGroup == null) {
                previousCallLogGroup = GroupedCallLogData(callLog)
            } else if (previousCallLogGroup.lastCallLog.localAddress.weakEqual(callLog.localAddress) &&
                previousCallLogGroup.lastCallLog.remoteAddress.weakEqual(callLog.remoteAddress)
            ) {
                if (TimestampUtils.isSameDay(previousCallLogGroup.lastCallLog.startDate, callLog.startDate)) {
                    previousCallLogGroup.callLogs.add(callLog)
                    previousCallLogGroup.lastCallLog = callLog
                } else {
                    list.add(previousCallLogGroup)
                    previousCallLogGroup = GroupedCallLogData(callLog)
                }
            } else {
                list.add(previousCallLogGroup)
                previousCallLogGroup = GroupedCallLogData(callLog)
            }

            if (LinphoneUtils.isCallLogMissed(callLog)) {
                if (previousMissedCallLogGroup == null) {
                    previousMissedCallLogGroup = GroupedCallLogData(callLog)
                } else if (previousMissedCallLogGroup.lastCallLog.localAddress.weakEqual(callLog.localAddress) &&
                    previousMissedCallLogGroup.lastCallLog.remoteAddress.weakEqual(callLog.remoteAddress)
                ) {
                    if (TimestampUtils.isSameDay(previousMissedCallLogGroup.lastCallLog.startDate, callLog.startDate)) {
                        previousMissedCallLogGroup.callLogs.add(callLog)
                        previousMissedCallLogGroup.lastCallLog = callLog
                    } else {
                        missedList.add(previousMissedCallLogGroup)
                        previousMissedCallLogGroup = GroupedCallLogData(callLog)
                    }
                } else {
                    missedList.add(previousMissedCallLogGroup)
                    previousMissedCallLogGroup = GroupedCallLogData(callLog)
                }
            }
        }

        if (previousCallLogGroup != null && !list.contains(previousCallLogGroup)) {
            list.add(previousCallLogGroup)
        }
        if (previousMissedCallLogGroup != null && !missedList.contains(previousMissedCallLogGroup)) {
            missedList.add(previousMissedCallLogGroup)
        }

        callLogs.value = list
        missedCallLogs.value = missedList
    }
}
