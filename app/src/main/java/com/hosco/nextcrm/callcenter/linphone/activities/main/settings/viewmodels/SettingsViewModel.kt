package org.linphone.activities.main.settings.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.main.settings.SettingListenerStub

class SettingsViewModel : ViewModel() {
    private val tunnelAvailable: Boolean = coreContext.core.tunnelAvailable()

    val showAccountSettings: Boolean = corePreferences.showAccountSettings
    val showTunnelSettings: Boolean = tunnelAvailable && corePreferences.showTunnelSettings
    val showAudioSettings: Boolean = corePreferences.showAudioSettings
    val showVideoSettings: Boolean = corePreferences.showVideoSettings
    val showCallSettings: Boolean = corePreferences.showCallSettings
    val showChatSettings: Boolean = corePreferences.showChatSettings
    val showNetworkSettings: Boolean = corePreferences.showNetworkSettings
    val showContactsSettings: Boolean = corePreferences.showContactsSettings
    val showAdvancedSettings: Boolean = corePreferences.showAdvancedSettings

    val accounts = MutableLiveData<ArrayList<AccountSettingsViewModel>>()

    private var accountClickListener = object : SettingListenerStub() {
        override fun onAccountClicked(identity: String) {
            accountsSettingsListener.onAccountClicked(identity)
        }
    }

    lateinit var accountsSettingsListener: SettingListenerStub

    lateinit var tunnelSettingsListener: SettingListenerStub

    lateinit var audioSettingsListener: SettingListenerStub

    lateinit var videoSettingsListener: SettingListenerStub

    lateinit var callSettingsListener: SettingListenerStub

    lateinit var chatSettingsListener: SettingListenerStub

    lateinit var networkSettingsListener: SettingListenerStub

    lateinit var contactsSettingsListener: SettingListenerStub

    lateinit var advancedSettingsListener: SettingListenerStub

    val primaryAccountDisplayNameListener = object : SettingListenerStub() {
        override fun onTextValueChanged(newValue: String) {
            val address = coreContext.core.createPrimaryContactParsed()
            address ?: return
            address.displayName = newValue
            address.username = primaryAccountUsername.value
            coreContext.core.primaryContact = address.asString()

            primaryAccountDisplayName.value = newValue
        }
    }
    val primaryAccountDisplayName = MutableLiveData<String>()

    val primaryAccountUsernameListener = object : SettingListenerStub() {
        override fun onTextValueChanged(newValue: String) {
            val address = coreContext.core.createPrimaryContactParsed()
            address ?: return
            address.username = newValue
            address.displayName = primaryAccountDisplayName.value
            coreContext.core.primaryContact = address.asString()

            primaryAccountUsername.value = newValue
        }
    }
    val primaryAccountUsername = MutableLiveData<String>()

    init {
        updateAccountsList()

        val address = coreContext.core.createPrimaryContactParsed()
        primaryAccountDisplayName.value = address?.displayName ?: ""
        primaryAccountUsername.value = address?.username ?: ""
    }

    override fun onCleared() {
        accounts.value.orEmpty().forEach(AccountSettingsViewModel::destroy)
        super.onCleared()
    }

    fun updateAccountsList() {
        accounts.value.orEmpty().forEach(AccountSettingsViewModel::destroy)

        val list = arrayListOf<AccountSettingsViewModel>()
        if (coreContext.core.accountList.isNotEmpty()) {
            for (account in coreContext.core.accountList) {
                val viewModel = AccountSettingsViewModel(account)
                viewModel.accountsSettingsListener = accountClickListener
                list.add(viewModel)
            }
        }

        accounts.value = list
    }
}
