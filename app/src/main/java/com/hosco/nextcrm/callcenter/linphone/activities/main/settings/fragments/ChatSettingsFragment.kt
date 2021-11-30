package org.linphone.activities.main.settings.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SettingsChatFragmentBinding
import org.linphone.activities.main.settings.viewmodels.ChatSettingsViewModel
import org.linphone.activities.navigateToEmptySetting
import org.linphone.compatibility.Compatibility
import org.linphone.mediastream.Version
import org.linphone.utils.Event

class ChatSettingsFragment : GenericSettingFragment<SettingsChatFragmentBinding>() {
    private lateinit var viewModel: ChatSettingsViewModel

    override fun getLayoutId(): Int = R.layout.settings_chat_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedMainViewModel = sharedViewModel

        viewModel = ViewModelProvider(this).get(ChatSettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }

        viewModel.launcherShortcutsEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { newValue ->
                    if (newValue) {
                        Compatibility.createShortcutsToChatRooms(requireContext())
                    } else {
                        Compatibility.removeShortcuts(requireContext())
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
                            getString(R.string.notification_channel_chat_id)
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

    override fun goBack() {
        if (sharedViewModel.isSlidingPaneSlideable.value == true) {
            sharedViewModel.closeSlidingPaneEvent.value = Event(true)
        } else {
            navigateToEmptySetting()
        }
    }
}
