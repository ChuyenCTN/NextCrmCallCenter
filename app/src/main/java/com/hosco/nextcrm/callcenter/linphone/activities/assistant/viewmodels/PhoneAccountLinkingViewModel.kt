
package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.*
import org.linphone.core.AccountCreator
import org.linphone.core.AccountCreatorListenerStub
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class PhoneAccountLinkingViewModelFactory(private val accountCreator: AccountCreator) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PhoneAccountLinkingViewModel(accountCreator) as T
    }
}

class PhoneAccountLinkingViewModel(accountCreator: AccountCreator) : AbstractPhoneViewModel(accountCreator) {
    val username = MutableLiveData<String>()

    val allowSkip = MutableLiveData<Boolean>()

    val linkEnabled: MediatorLiveData<Boolean> = MediatorLiveData()

    val waitForServerAnswer = MutableLiveData<Boolean>()

    val leaveAssistantEvent = MutableLiveData<Event<Boolean>>()

    val goToSmsValidationEvent = MutableLiveData<Event<Boolean>>()

    val onErrorEvent: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }

    private val listener = object : AccountCreatorListenerStub() {
        override fun onIsAliasUsed(
            creator: AccountCreator,
            status: AccountCreator.Status,
            response: String?
        ) {
            Log.i("[Phone Account Linking] onIsAliasUsed status is $status")

            when (status) {
                AccountCreator.Status.AliasNotExist -> {
                    if (creator.linkAccount() != AccountCreator.Status.RequestOk) {
                        Log.e("[Phone Account Linking] linkAccount status is $status")
                        waitForServerAnswer.value = false
                        onErrorEvent.value = Event("Error: ${status.name}")
                    }
                }
                AccountCreator.Status.AliasExist, AccountCreator.Status.AliasIsAccount -> {
                    waitForServerAnswer.value = false
                    onErrorEvent.value = Event("Error: ${status.name}")
                }
                else -> {
                    waitForServerAnswer.value = false
                    onErrorEvent.value = Event("Error: ${status.name}")
                }
            }
        }

        override fun onLinkAccount(
            creator: AccountCreator,
            status: AccountCreator.Status,
            response: String?
        ) {
            Log.i("[Phone Account Linking] onLinkAccount status is $status")
            waitForServerAnswer.value = false

            when (status) {
                AccountCreator.Status.RequestOk -> {
                    goToSmsValidationEvent.value = Event(true)
                }
                else -> {
                    onErrorEvent.value = Event("Error: ${status.name}")
                }
            }
        }
    }

    init {
        accountCreator.addListener(listener)

        linkEnabled.value = false
        linkEnabled.addSource(prefix) {
            linkEnabled.value = isLinkButtonEnabled()
        }
        linkEnabled.addSource(phoneNumber) {
            linkEnabled.value = isLinkButtonEnabled()
        }
        linkEnabled.addSource(phoneNumberError) {
            linkEnabled.value = isLinkButtonEnabled()
        }
    }

    override fun onCleared() {
        accountCreator.removeListener(listener)
        super.onCleared()
    }

    fun link() {
        accountCreator.setPhoneNumber(phoneNumber.value, prefix.value)
        accountCreator.username = username.value
        Log.i("[Assistant] [Phone Account Linking] Phone number is ${accountCreator.phoneNumber}")

        waitForServerAnswer.value = true
        val status: AccountCreator.Status = accountCreator.isAliasUsed
        Log.i("[Phone Account Linking] isAliasUsed returned $status")
        if (status != AccountCreator.Status.RequestOk) {
            waitForServerAnswer.value = false
            onErrorEvent.value = Event("Error: ${status.name}")
        }
    }

    fun skip() {
        leaveAssistantEvent.value = Event(true)
    }

    private fun isLinkButtonEnabled(): Boolean {
        return isPhoneNumberOk()
    }
}
