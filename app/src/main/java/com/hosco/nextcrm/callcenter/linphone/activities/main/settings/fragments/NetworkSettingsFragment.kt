package org.linphone.activities.main.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SettingsNetworkFragmentBinding
import org.linphone.activities.main.settings.viewmodels.NetworkSettingsViewModel
import org.linphone.activities.navigateToEmptySetting
import org.linphone.utils.Event

class NetworkSettingsFragment : GenericSettingFragment<SettingsNetworkFragmentBinding>() {
    private lateinit var viewModel: NetworkSettingsViewModel

    override fun getLayoutId(): Int = R.layout.settings_network_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedMainViewModel = sharedViewModel

        viewModel = ViewModelProvider(this).get(NetworkSettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }
    }

    override fun goBack() {
        if (sharedViewModel.isSlidingPaneSlideable.value == true) {
            sharedViewModel.closeSlidingPaneEvent.value = Event(true)
        } else {
            navigateToEmptySetting()
        }
    }
}
