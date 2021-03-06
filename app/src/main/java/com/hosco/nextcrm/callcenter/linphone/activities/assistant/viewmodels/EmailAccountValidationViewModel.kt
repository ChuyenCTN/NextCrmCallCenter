package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.linphone.core.AccountCreator
import org.linphone.core.AccountCreatorListenerStub
import org.linphone.core.ProxyConfig
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class EmailAccountValidationViewModelFactory(private val accountCreator: AccountCreator) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EmailAccountValidationViewModel(accountCreator) as T
    }
}

class EmailAccountValidationViewModel(val accountCreator: AccountCreator) : ViewModel() {
    val email = MutableLiveData<String>()

    val waitForServerAnswer = MutableLiveData<Boolean>()

    val leaveAssistantEvent = MutableLiveData<Event<Boolean>>()

    val onErrorEvent: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }

    private val listener = object : AccountCreatorListenerStub() {
        override fun onIsAccountActivated(
            creator: AccountCreator,
            status: AccountCreator.Status,
            response: String?
        ) {
            Log.i("[Account Validation] onIsAccountActivated status is $status")
            waitForServerAnswer.value = false

            when (status) {
                AccountCreator.Status.AccountActivated -> {
                    if (createProxyConfig()) {
                        leaveAssistantEvent.value = Event(true)
                    } else {
                        onErrorEvent.value = Event("Error: ${status.name}")
                    }
                }
                AccountCreator.Status.AccountNotActivated -> {
                    onErrorEvent.value = Event("Error: ${status.name}")
                }
                else -> {
                    onErrorEvent.value = Event("Error: ${status.name}")
                }
            }
        }
    }

    init {
        accountCreator.addListener(listener)
        email.value = accountCreator.email
    }

    override fun onCleared() {
        accountCreator.removeListener(listener)
        super.onCleared()
    }

    fun finish() {
        waitForServerAnswer.value = true
        val status = accountCreator.isAccountActivated
        Log.i("[Assistant] [Account Validation] Account exists returned $status")
        if (status != AccountCreator.Status.RequestOk) {
            waitForServerAnswer.value = false
            onErrorEvent.value = Event("Error: ${status.name}")
        }
    }

    private fun createProxyConfig(): Boolean {
        val proxyConfig: ProxyConfig? = accountCreator.createProxyConfig()

        if (proxyConfig == null) {
            Log.e("[Assistant] [Account Validation] Account creator couldn't create proxy config")
            onErrorEvent.value = Event("Error: Failed to create account object")
            return false
        }

        proxyConfig.isPushNotificationAllowed = true

        Log.i("[Assistant] [Account Validation] Proxy config created")
        return true
    }
}
