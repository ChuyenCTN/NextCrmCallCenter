package org.linphone.activities.main.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SettingsAccountFragmentBinding
import org.linphone.activities.main.settings.viewmodels.AccountSettingsViewModel
import org.linphone.activities.main.settings.viewmodels.AccountSettingsViewModelFactory
import org.linphone.activities.navigateToEmptySetting
import org.linphone.activities.navigateToPhoneLinking
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class AccountSettingsFragment : GenericSettingFragment<SettingsAccountFragmentBinding>() {
    private lateinit var viewModel: AccountSettingsViewModel

    override fun getLayoutId(): Int = R.layout.settings_account_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedMainViewModel = sharedViewModel

        val identity = arguments?.getString("Identity")
        if (identity == null) {
            Log.e("[Account Settings] Identity is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            goBack()
            return
        }

        try {
            viewModel = ViewModelProvider(this, AccountSettingsViewModelFactory(identity)).get(
                AccountSettingsViewModel::class.java
            )
        } catch (nsee: NoSuchElementException) {
            Log.e("[Account Settings] Failed to find Account object, aborting!")
            goBack()
            return
        }
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }

        viewModel.linkPhoneNumberEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    val authInfo = viewModel.account.findAuthInfo()
                    if (authInfo == null) {
                        Log.e("[Account Settings] Failed to find auth info for account ${viewModel.account}")
                    } else {
                        val args = Bundle()
                        args.putString("Username", authInfo.username)
                        args.putString("Password", authInfo.password)
                        args.putString("HA1", authInfo.ha1)
                        navigateToPhoneLinking(args)
                    }
                }
            }
        )

        viewModel.accountRemovedEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    sharedViewModel.accountRemoved.value = true
                    goBack()
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
