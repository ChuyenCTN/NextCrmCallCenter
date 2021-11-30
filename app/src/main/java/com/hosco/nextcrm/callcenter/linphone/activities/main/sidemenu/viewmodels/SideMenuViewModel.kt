package org.linphone.activities.main.sidemenu.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.main.settings.SettingListenerStub
import org.linphone.activities.main.settings.viewmodels.AccountSettingsViewModel
import org.linphone.core.*

class SideMenuViewModel : ViewModel() {
    val showAccounts: Boolean = corePreferences.showAccountsInSideMenu
    val showAssistant: Boolean = corePreferences.showAssistantInSideMenu
    val showSettings: Boolean = corePreferences.showSettingsInSideMenu
    val showRecordings: Boolean = corePreferences.showRecordingsInSideMenu
    val showAbout: Boolean = corePreferences.showAboutInSideMenu
    val showQuit: Boolean = corePreferences.showQuitInSideMenu

    val defaultAccountViewModel = MutableLiveData<AccountSettingsViewModel>()
    val defaultAccountFound = MutableLiveData<Boolean>()
    val defaultAccountAvatar = MutableLiveData<String>()

    val accounts = MutableLiveData<ArrayList<AccountSettingsViewModel>>()

    lateinit var accountsSettingsListener: SettingListenerStub

    private val listener: CoreListenerStub = object : CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(
            core: Core,
            account: Account,
            state: RegistrationState,
            message: String
        ) {
            // +1 is for the default account, otherwise this will trigger every time
            if (coreContext.core.accountList.size != accounts.value.orEmpty().size + 1) {
                // Only refresh the list if an account has been added or removed
                updateAccountsList()
            }
        }
    }

    init {
        defaultAccountFound.value = false
        defaultAccountAvatar.value = corePreferences.defaultAccountAvatarPath
        coreContext.core.addListener(listener)
        updateAccountsList()
    }

    override fun onCleared() {
        defaultAccountViewModel.value?.destroy()
        accounts.value.orEmpty().forEach(AccountSettingsViewModel::destroy)
        coreContext.core.removeListener(listener)
        super.onCleared()
    }

    fun updateAccountsList() {
        defaultAccountFound.value = false // Do not assume a default account will still be found
        defaultAccountViewModel.value?.destroy()
        accounts.value.orEmpty().forEach(AccountSettingsViewModel::destroy)

        val list = arrayListOf<AccountSettingsViewModel>()
        if (coreContext.core.accountList.isNotEmpty()) {
            val defaultAccount = coreContext.core.defaultAccount
            if (defaultAccount != null) {
                val defaultViewModel = AccountSettingsViewModel(defaultAccount)
                defaultViewModel.accountsSettingsListener = object : SettingListenerStub() {
                    override fun onAccountClicked(identity: String) {
                        accountsSettingsListener.onAccountClicked(identity)
                    }
                }
                defaultAccountViewModel.value = defaultViewModel
                defaultAccountFound.value = true
            }

            for (account in coreContext.core.accountList) {
                if (account != coreContext.core.defaultAccount) {
                    val viewModel = AccountSettingsViewModel(account)
                    viewModel.accountsSettingsListener = object : SettingListenerStub() {
                        override fun onAccountClicked(identity: String) {
                            accountsSettingsListener.onAccountClicked(identity)
                        }
                    }
                    list.add(viewModel)
                }
            }
        }
        accounts.value = list
    }

    fun setPictureFromPath(picturePath: String) {
        corePreferences.defaultAccountAvatarPath = picturePath
        defaultAccountAvatar.value = corePreferences.defaultAccountAvatarPath
        coreContext.contactsManager.updateLocalContacts()
    }
}
