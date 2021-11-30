package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.core.ConfiguringState
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class RemoteProvisioningViewModel : ViewModel() {
    val urlToFetch = MutableLiveData<String>()
    val urlError = MutableLiveData<String>()

    val fetchEnabled: MediatorLiveData<Boolean> = MediatorLiveData()
    val fetchInProgress = MutableLiveData<Boolean>()
    val fetchSuccessfulEvent = MutableLiveData<Event<Boolean>>()

    private val listener = object : CoreListenerStub() {
        override fun onConfiguringStatus(core: Core, status: ConfiguringState, message: String?) {
            fetchInProgress.value = false
            when (status) {
                ConfiguringState.Successful -> {
                    fetchSuccessfulEvent.value = Event(true)
                }
                ConfiguringState.Failed -> {
                    fetchSuccessfulEvent.value = Event(false)
                }
            }
        }
    }

    init {
        fetchInProgress.value = false
        coreContext.core.addListener(listener)

        fetchEnabled.value = false
        fetchEnabled.addSource(urlToFetch) {
            fetchEnabled.value = isFetchEnabled()
        }
        fetchEnabled.addSource(urlError) {
            fetchEnabled.value = isFetchEnabled()
        }
    }

    override fun onCleared() {
        coreContext.core.removeListener(listener)
        super.onCleared()
    }

    fun fetchAndApply() {
        val url = urlToFetch.value.orEmpty()
        coreContext.core.provisioningUri = url
        Log.w("[Remote Provisioning] Url set to [$url], restarting Core")
        fetchInProgress.value = true
        coreContext.core.stop()
        coreContext.core.start()
    }

    private fun isFetchEnabled(): Boolean {
        return urlToFetch.value.orEmpty().isNotEmpty() && urlError.value.orEmpty().isEmpty()
    }
}
