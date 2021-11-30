package org.linphone.activities.main.settings.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SettingsCallFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.main.settings.viewmodels.CallSettingsViewModel
import org.linphone.activities.navigateToEmptySetting
import org.linphone.compatibility.Compatibility
import org.linphone.core.tools.Log
import org.linphone.mediastream.Version
import org.linphone.telecom.TelecomHelper
import org.linphone.utils.Event
import org.linphone.utils.PermissionHelper

class CallSettingsFragment : GenericSettingFragment<SettingsCallFragmentBinding>() {
    private lateinit var viewModel: CallSettingsViewModel

    override fun getLayoutId(): Int = R.layout.settings_call_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedMainViewModel = sharedViewModel

        viewModel = ViewModelProvider(this).get(CallSettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }

        viewModel.systemWideOverlayEnabledEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    if (!Compatibility.canDrawOverlay(requireContext())) {
                        val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:${requireContext().packageName}"))
                        startActivityForResult(intent, 0)
                    }
                }
            }
        )

        viewModel.goToAndroidNotificationSettingsEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    if (Build.VERSION.SDK_INT >= Version.API26_O_80) {
                        val i = Intent()
                        i.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                        i.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        i.putExtra(
                            Settings.EXTRA_CHANNEL_ID,
                            getString(R.string.notification_channel_service_id)
                        )
                        i.addCategory(Intent.CATEGORY_DEFAULT)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        startActivity(i)
                    }
                }
            }
        )

        viewModel.enableTelecomManagerEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    if (!PermissionHelper.get().hasTelecomManagerPermissions()) {
                        val permissions = arrayOf(
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.MANAGE_OWN_CALLS
                        )
                        requestPermissions(permissions, 1)
                    } else if (!TelecomHelper.exists()) {
                        Log.w("[Telecom Helper] Doesn't exists yet, creating it")
                        TelecomHelper.create(requireContext())
                    }
                }
            }
        )

        viewModel.goToAndroidNotificationSettingsEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    if (Build.VERSION.SDK_INT >= Version.API26_O_80) {
                        val i = Intent()
                        i.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                        i.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        i.putExtra(
                            Settings.EXTRA_CHANNEL_ID,
                            getString(R.string.notification_channel_service_id)
                        )
                        i.addCategory(Intent.CATEGORY_DEFAULT)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                        startActivity(i)
                    }
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && !Compatibility.canDrawOverlay(requireContext())) {
            viewModel.overlayListener.onBoolValueChanged(false)
        } else if (requestCode == 1) {
            if (!TelecomHelper.exists()) {
                Log.w("[Telecom Helper] Doesn't exists yet, creating it")
                TelecomHelper.create(requireContext())
            }
            updateTelecomManagerAccount()
        }
    }

    private fun updateTelecomManagerAccount() {
        if (!TelecomHelper.exists()) {
            Log.e("[Telecom Helper] Doesn't exists, can't update account!")
            return
        }
        // We have to refresh the account object otherwise isAccountEnabled will always return false...
        val account = TelecomHelper.get().findExistingAccount(requireContext())
        TelecomHelper.get().updateAccount(account)
        val enabled = TelecomHelper.get().isAccountEnabled()
        viewModel.useTelecomManager.value = enabled
        corePreferences.useTelecomManager = enabled
    }

    override fun goBack() {
        if (sharedViewModel.isSlidingPaneSlideable.value == true) {
            sharedViewModel.closeSlidingPaneEvent.value = Event(true)
        } else {
            navigateToEmptySetting()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        for (index in grantResults.indices) {
            val result = grantResults[index]
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.w("[Call Settings] ${permissions[index]} permission denied but required for telecom manager")
                viewModel.useTelecomManager.value = false
                corePreferences.useTelecomManager = false
                return
            }
        }

        TelecomHelper.create(requireContext())
    }
}
