package org.linphone.activities.main.settings.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SettingsContactsFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.main.settings.viewmodels.ContactsSettingsViewModel
import org.linphone.activities.navigateToEmptySetting
import org.linphone.compatibility.Compatibility
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.PermissionHelper

class ContactsSettingsFragment : GenericSettingFragment<SettingsContactsFragmentBinding>() {
    private lateinit var viewModel: ContactsSettingsViewModel

    override fun getLayoutId(): Int = R.layout.settings_contacts_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedMainViewModel = sharedViewModel

        viewModel = ViewModelProvider(this).get(ContactsSettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }

        viewModel.launcherShortcutsEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { newValue ->
                    if (newValue) {
                        Compatibility.createShortcutsToContacts(requireContext())
                    } else {
                        Compatibility.removeShortcuts(requireContext())
                        if (corePreferences.chatRoomShortcuts) {
                            Compatibility.createShortcutsToChatRooms(requireContext())
                        }
                    }
                }
            }
        )

        viewModel.askWriteContactsPermissionForPresenceStorageEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    Log.i("[Contacts Settings] Asking for WRITE_CONTACTS permission to be able to store presence")
                    requestPermissions(arrayOf(android.Manifest.permission.WRITE_CONTACTS), 1)
                }
            }
        )

        if (!PermissionHelper.required(requireContext()).hasReadContactsPermission()) {
            Log.i("[Contacts Settings] Asking for READ_CONTACTS permission")
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    Log.i("[Contacts Settings] READ_CONTACTS permission granted")
                    viewModel.readContactsPermissionGranted.value = true
                    coreContext.contactsManager.onReadContactsPermissionGranted()
                    coreContext.contactsManager.fetchContactsAsync()
                } else {
                    Log.w("[Contacts Settings] READ_CONTACTS permission denied")
                }
            }
            1 -> {
                val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (granted) {
                    Log.i("[Contacts Settings] WRITE_CONTACTS permission granted")
                    corePreferences.storePresenceInNativeContact = true
                    coreContext.contactsManager.storePresenceInformationForAllContacts()
                } else {
                    Log.w("[Contacts Settings] WRITE_CONTACTS permission denied")
                }
            }
        }
    }

    override fun goBack() {
        if (sharedViewModel.isSlidingPaneSlideable.value == true) {
            sharedViewModel.closeSlidingPaneEvent.value = Event(true)
        } else {
            navigateToEmptySetting()
        }
    }
}
