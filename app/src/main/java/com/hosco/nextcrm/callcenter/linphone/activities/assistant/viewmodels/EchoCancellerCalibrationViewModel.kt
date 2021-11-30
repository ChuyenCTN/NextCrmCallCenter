
package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.EcCalibratorStatus
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class EchoCancellerCalibrationViewModel : ViewModel() {
    val echoCalibrationTerminated = MutableLiveData<Event<Boolean>>()

    private val listener = object : CoreListenerStub() {
        override fun onEcCalibrationResult(core: Core, status: EcCalibratorStatus, delayMs: Int) {
            if (status == EcCalibratorStatus.InProgress) return
            echoCancellerCalibrationFinished(status, delayMs)
        }
    }

    init {
        coreContext.core.addListener(listener)
    }

    fun startEchoCancellerCalibration() {
        coreContext.core.startEchoCancellerCalibration()
    }

    fun echoCancellerCalibrationFinished(status: EcCalibratorStatus, delay: Int) {
        coreContext.core.removeListener(listener)
        when (status) {
            EcCalibratorStatus.DoneNoEcho -> {
                Log.i("[Echo Canceller Calibration] Done, no echo")
            }
            EcCalibratorStatus.Done -> {
                Log.i("[Echo Canceller Calibration] Done, delay is ${delay}ms")
            }
            EcCalibratorStatus.Failed -> {
                Log.w("[Echo Canceller Calibration] Failed")
            }
        }
        echoCalibrationTerminated.value = Event(true)
    }
}
