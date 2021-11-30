package org.linphone.activities.call.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.core.*
import org.linphone.utils.Event

class IncomingCallViewModelFactory(private val call: Call) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IncomingCallViewModel(call) as T
    }
}

class IncomingCallViewModel(call: Call) : CallViewModel(call) {
    val screenLocked = MutableLiveData<Boolean>()

    val earlyMediaVideoEnabled = MutableLiveData<Boolean>()

    val inviteWithVideo = MutableLiveData<Boolean>()

    private val listener = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State,
            message: String
        ) {
            if (core.callsNb == 0) {
                callEndedEvent.value = Event(true)
            }
        }
    }

    init {
        coreContext.core.addListener(listener)

        screenLocked.value = false
        inviteWithVideo.value = call.remoteParams?.videoEnabled() == true && coreContext.core.videoActivationPolicy.automaticallyAccept
        earlyMediaVideoEnabled.value = corePreferences.acceptEarlyMedia &&
            call.state == Call.State.IncomingEarlyMedia &&
            call.currentParams.videoEnabled()

    }

    override fun onCleared() {
        coreContext.core.removeListener(listener)
        super.onCleared()
    }

    fun answer(doAction: Boolean) {
        if (doAction) coreContext.answerCall(call)
    }

    fun decline(doAction: Boolean) {
        if (doAction) coreContext.declineCall(call)
    }

    private fun findIncomingCall(): Call? {
        for (call in coreContext.core.calls) {
            if (call.state == Call.State.IncomingReceived ||
                call.state == Call.State.IncomingEarlyMedia
            ) {
                return call
            }
        }
        return null
    }
}
