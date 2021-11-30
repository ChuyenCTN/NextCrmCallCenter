package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.linphone.core.AccountCreator
import org.linphone.core.AccountCreatorListenerStub
import org.linphone.core.ProxyConfig
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class PhoneAccountValidationViewModelFactory(private val accountCreator: AccountCreator) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PhoneAccountValidationViewModel(accountCreator) as T
    }
}

class PhoneAccountValidationViewModel(val accountCreator: AccountCreator) : ViewModel() {
    val phoneNumber = MutableLiveData<String>()

    val code = MutableLiveData<String>()

    val isLogin = MutableLiveData<Boolean>()

    val isCreation = MutableLiveData<Boolean>()

    val isLinking = MutableLiveData<Boolean>()

    val waitForServerAnswer = MutableLiveData<Boolean>()

    val leaveAssistantEvent = MutableLiveData<Event<Boolean>>()

    val onErrorEvent: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }

    val listener = object : AccountCreatorListenerStub() {
        override fun onLoginLinphoneAccount(
            creator: AccountCreator,
            status: AccountCreator.Status,
            response: String?
        ) {
            Log.i("[Assistant] [Phone Account Validation] onLoginLinphoneAccount status is $status")
            waitForServerAnswer.value = false

            if (status == AccountCreator.Status.RequestOk) {
                if (createProxyConfig()) {
                    leaveAssistantEvent.value = Event(true)
                } else {
                    onErrorEvent.value = Event("Error: Failed to create account object")
                }
            } else {
                onErrorEvent.value = Event("Error: ${status.name}")
            }
        }

        override fun onActivateAlias(
            creator: AccountCreator,
            status: AccountCreator.Status,
            response: String?
        ) {
            Log.i("[Assistant] [Phone Account Validation] onActivateAlias status is $status")
            waitForServerAnswer.value = false

            when (status) {
                AccountCreator.Status.AccountActivated -> {
                    leaveAssistantEvent.value = Event(true)
                }
                else -> {
                    onErrorEvent.value = Event("Error: ${status.name}")
                }
            }
        }

        override fun onActivateAccount(
            creator: AccountCreator,
            status: AccountCreator.Status,
            response: String?
        ) {
            Log.i("[Assistant] [Phone Account Validation] onActivateAccount status is $status")
            waitForServerAnswer.value = false

            if (status == AccountCreator.Status.AccountActivated) {
                if (createProxyConfig()) {
                    leaveAssistantEvent.value = Event(true)
                } else {
                    onErrorEvent.value = Event("Error: Failed to create account object")
                }
            } else {
                onErrorEvent.value = Event("Error: ${status.name}")
            }
        }
    }

    init {
        accountCreator.addListener(listener)
    }

    override fun onCleared() {
        accountCreator.removeListener(listener)
        super.onCleared()
    }

    fun finish() {
        accountCreator.activationCode = code.value.orEmpty()
        Log.i("[Assistant] [Phone Account Validation] Phone number is ${accountCreator.phoneNumber} and activation code is ${accountCreator.activationCode}")
        waitForServerAnswer.value = true

        val status = when {
            isLogin.value == true -> accountCreator.loginLinphoneAccount()
            isCreation.value == true -> accountCreator.activateAccount()
            isLinking.value == true -> accountCreator.activateAlias()
            else -> AccountCreator.Status.UnexpectedError
        }
        Log.i("[Assistant] [Phone Account Validation] Code validation result is $status")
        if (status != AccountCreator.Status.RequestOk) {
            waitForServerAnswer.value = false
            onErrorEvent.value = Event("Error: ${status.name}")
        }
    }

    private fun createProxyConfig(): Boolean {
        val proxyConfig: ProxyConfig? = accountCreator.createProxyConfig()

        if (proxyConfig == null) {
            Log.e("[Assistant] [Phone Account Validation] Account creator couldn't create proxy config")
            return false
        }

        proxyConfig.isPushNotificationAllowed = true
        Log.i("[Assistant] [Phone Account Validation] Proxy config created")
        return true
    }
}
