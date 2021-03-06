package org.linphone.activities.main.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.transition.MaterialSharedAxis
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SettingsFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.*
import org.linphone.activities.main.fragments.SecureFragment
import org.linphone.activities.main.settings.SettingListenerStub
import org.linphone.activities.main.settings.viewmodels.SettingsViewModel
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log

class SettingsFragment : SecureFragment<SettingsFragmentBinding>() {
    private lateinit var sharedViewModel: SharedMainViewModel
    private lateinit var viewModel: SettingsViewModel

    override fun getLayoutId(): Int = R.layout.settings_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        useMaterialSharedAxisXForwardAnimation = false
        if (corePreferences.enableAnimations) {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        }

        /* Shared view model & sliding pane related */

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        view.doOnPreDraw { sharedViewModel.isSlidingPaneSlideable.value = binding.slidingPane.isSlideable }

        sharedViewModel.closeSlidingPaneEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    if (!binding.slidingPane.closePane()) {
                        goBack()
                    }
                }
            }
        )
        sharedViewModel.layoutChangedEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    sharedViewModel.isSlidingPaneSlideable.value = binding.slidingPane.isSlideable
                    if (binding.slidingPane.isSlideable) {
                        val navHostFragment = childFragmentManager.findFragmentById(R.id.settings_nav_container) as NavHostFragment
                        if (navHostFragment.navController.currentDestination?.id == R.id.emptySettingsFragment) {
                            Log.i("[Settings] Foldable device has been folded, closing side pane with empty fragment")
                            binding.slidingPane.closePane()
                        }
                    }
                }
            }
        )
        binding.slidingPane.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        /* End of shared view model & sliding pane related */

        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }

        sharedViewModel.accountRemoved.observe(
            viewLifecycleOwner,
            {
                Log.i("[Settings] Account removed, update accounts list")
                viewModel.updateAccountsList()
            }
        )

        val identity = arguments?.getString("Identity")
        if (identity != null) {
            Log.i("[Settings] Found identity parameter in arguments: $identity")
            arguments?.clear()
            navigateToAccountSettings(identity, binding.slidingPane)
        }

        viewModel.accountsSettingsListener = object : SettingListenerStub() {
            override fun onAccountClicked(identity: String) {
                Log.i("[Settings] Navigation to settings for account with identity: $identity")
                navigateToAccountSettings(identity, binding.slidingPane)
            }
        }

        viewModel.tunnelSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToTunnelSettings(binding.slidingPane)
            }
        }

        viewModel.audioSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToAudioSettings(binding.slidingPane)
            }
        }

        viewModel.videoSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToVideoSettings(binding.slidingPane)
            }
        }

        viewModel.callSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToCallSettings(binding.slidingPane)
            }
        }

        viewModel.chatSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToChatSettings(binding.slidingPane)
            }
        }

        viewModel.networkSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToNetworkSettings(binding.slidingPane)
            }
        }

        viewModel.contactsSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToContactsSettings(binding.slidingPane)
            }
        }

        viewModel.advancedSettingsListener = object : SettingListenerStub() {
            override fun onClicked() {
                navigateToAdvancedSettings(binding.slidingPane)
            }
        }
    }
}
