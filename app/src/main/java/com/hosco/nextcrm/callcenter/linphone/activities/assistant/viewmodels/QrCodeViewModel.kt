package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class QrCodeViewModel : ViewModel() {
    val qrCodeFoundEvent = MutableLiveData<Event<String>>()

    val showSwitchCamera = MutableLiveData<Boolean>()

    private val listener = object : CoreListenerStub() {
        override fun onQrcodeFound(core: Core, result: String?) {
            Log.i("[QR Code] Found [$result]")
            if (result != null) qrCodeFoundEvent.postValue(Event(result))
        }
    }

    init {
        coreContext.core.addListener(listener)
        showSwitchCamera.value = coreContext.showSwitchCameraButton()
    }

    override fun onCleared() {
        coreContext.core.removeListener(listener)
        super.onCleared()
    }

    fun setBackCamera() {
        showSwitchCamera.value = coreContext.showSwitchCameraButton()

        for (camera in coreContext.core.videoDevicesList) {
            if (camera.contains("Back")) {
                Log.i("[QR Code] Found back facing camera: $camera")
                coreContext.core.videoDevice = camera
                return
            }
        }

        val first = coreContext.core.videoDevicesList.firstOrNull()
        if (first != null) {
            Log.i("[QR Code] Using first camera found: $first")
            coreContext.core.videoDevice = first
        }
    }

    fun switchCamera() {
        coreContext.switchCamera()
    }
}
