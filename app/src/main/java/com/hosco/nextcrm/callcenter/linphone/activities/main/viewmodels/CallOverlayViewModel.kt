package org.linphone.activities.main.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub

class CallOverlayViewModel : ViewModel() {
    val displayCallOverlay = MutableLiveData<Boolean>()

    private val listener = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State,
            message: String
        ) {
            if (state == Call.State.IncomingReceived || state == Call.State.OutgoingInit) {
                createCallOverlay()
            } else if (state == Call.State.End || state == Call.State.Error || state == Call.State.Released) {
                if (core.callsNb == 0) {
                    removeCallOverlay()
                }
            }
        }
    }

    init {
        displayCallOverlay.value = corePreferences.showCallOverlay &&
            !corePreferences.systemWideCallOverlay &&
            coreContext.core.callsNb > 0

        coreContext.core.addListener(listener)
    }

    override fun onCleared() {
        coreContext.core.removeListener(listener)

        super.onCleared()
    }

    private fun createCallOverlay() {
        // If overlay is disabled or if system-wide call overlay is enabled, abort
        if (!corePreferences.showCallOverlay || corePreferences.systemWideCallOverlay) {
            return
        }

        displayCallOverlay.value = true
    }

    private fun removeCallOverlay() {
        displayCallOverlay.value = false
    }
}
