package org.linphone.activities.main.settings.viewmodels

import androidx.lifecycle.MutableLiveData
import org.linphone.activities.main.settings.SettingListenerStub
import org.linphone.utils.Event
import org.linphone.utils.PermissionHelper

class ContactsSettingsViewModel : GenericSettingsViewModel() {
    val askWriteContactsPermissionForPresenceStorageEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    val readContactsPermissionGranted = MutableLiveData<Boolean>()

    val friendListSubscribeListener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            core.enableFriendListSubscription(newValue)
        }
    }
    val friendListSubscribe = MutableLiveData<Boolean>()

    val showNewContactAccountDialogListener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            prefs.showNewContactAccountDialog = newValue
        }
    }
    val showNewContactAccountDialog = MutableLiveData<Boolean>()

    val nativePresenceListener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            if (newValue) {
                if (PermissionHelper.get().hasWriteContactsPermission()) {
                    prefs.storePresenceInNativeContact = newValue
                } else {
                    askWriteContactsPermissionForPresenceStorageEvent.value = Event(true)
                }
            } else {
                prefs.storePresenceInNativeContact = newValue
            }
        }
    }
    val nativePresence = MutableLiveData<Boolean>()

    val showOrganizationListener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            prefs.displayOrganization = newValue
        }
    }
    val showOrganization = MutableLiveData<Boolean>()

    val launcherShortcutsListener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            prefs.contactsShortcuts = newValue
            launcherShortcutsEvent.value = Event(newValue)
        }
    }
    val launcherShortcuts = MutableLiveData<Boolean>()
    val launcherShortcutsEvent = MutableLiveData<Event<Boolean>>()

    init {
        readContactsPermissionGranted.value = PermissionHelper.get().hasReadContactsPermission()

        friendListSubscribe.value = core.isFriendListSubscriptionEnabled
        showNewContactAccountDialog.value = prefs.showNewContactAccountDialog
        nativePresence.value = prefs.storePresenceInNativeContact
        showOrganization.value = prefs.displayOrganization
        launcherShortcuts.value = prefs.contactsShortcuts
    }
}
