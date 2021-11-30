package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.core.*
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class GenericLoginViewModelFactory(private val accountCreator: AccountCreator) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GenericLoginViewModel(accountCreator) as T
    }
}

class GenericLoginViewModel(private val accountCreator: AccountCreator) : ViewModel() {
    val username = MutableLiveData<String>()

    val password = MutableLiveData<String>()

    val domain = MutableLiveData<String>()

    val displayName = MutableLiveData<String>()

    val transport = MutableLiveData<TransportType>()

    val loginEnabled: MediatorLiveData<Boolean> = MediatorLiveData()

    val waitForServerAnswer = MutableLiveData<Boolean>()

    val leaveAssistantEvent = MutableLiveData<Event<Boolean>>()

    val invalidCredentialsEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    val onErrorEvent: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }

    private var proxyConfigToCheck: ProxyConfig? = null

    private val coreListener = object : CoreListenerStub() {
        override fun onRegistrationStateChanged(
            core: Core,
            cfg: ProxyConfig,
            state: RegistrationState,
            message: String
        ) {
            if (cfg == proxyConfigToCheck) {
                Log.i("[Assistant] [Generic Login] Registration state is $state: $message")
                if (state == RegistrationState.Ok) {
                    waitForServerAnswer.value = false
                    leaveAssistantEvent.value = Event(true)
                    core.removeListener(this)
                } else if (state == RegistrationState.Failed) {
                    waitForServerAnswer.value = false
                    invalidCredentialsEvent.value = Event(true)
                    core.removeListener(this)
                }
            }
        }
    }

    init {
        transport.value = TransportType.Tls

        loginEnabled.value = false
        loginEnabled.addSource(username) {
            loginEnabled.value = isLoginButtonEnabled()
        }
        loginEnabled.addSource(password) {
            loginEnabled.value = isLoginButtonEnabled()
        }
        loginEnabled.addSource(domain) {
            loginEnabled.value = isLoginButtonEnabled()
        }
    }

    fun setTransport(transportType: TransportType) {
        transport.value = transportType
    }

    fun removeInvalidProxyConfig() {
        val cfg = proxyConfigToCheck
        cfg ?: return
        val authInfo = cfg.findAuthInfo()
        if (authInfo != null) coreContext.core.removeAuthInfo(authInfo)
        coreContext.core.removeProxyConfig(cfg)
        proxyConfigToCheck = null
    }

    fun continueEvenIfInvalidCredentials() {
        leaveAssistantEvent.value = Event(true)
    }

    fun createProxyConfig() {
        waitForServerAnswer.value = true
        coreContext.core.addListener(coreListener)

        accountCreator.username = username.value
        accountCreator.password = password.value
        accountCreator.domain = domain.value
        accountCreator.displayName = displayName.value
        accountCreator.transport = transport.value

        val proxyConfig: ProxyConfig? = accountCreator.createProxyConfig()
        proxyConfigToCheck = proxyConfig

        if (proxyConfig == null) {
            Log.e("[Assistant] [Generic Login] Account creator couldn't create proxy config")
            coreContext.core.removeListener(coreListener)
            onErrorEvent.value = Event("Error: Failed to create account object")
            return
        }

        Log.i("[Assistant] [Generic Login] Proxy config created")
        // The following is required to keep the app alive
        // and be able to receive calls while in background
        if (domain.value.orEmpty() != corePreferences.defaultDomain) {
            Log.i("[Assistant] [Generic Login] Background mode with foreground service automatically enabled")
            corePreferences.keepServiceAlive = true
            coreContext.notificationsManager.startForeground()
        }
    }

    private fun isLoginButtonEnabled(): Boolean {
        return username.value.orEmpty().isNotEmpty() && domain.value.orEmpty().isNotEmpty() && password.value.orEmpty().isNotEmpty()
    }
}
