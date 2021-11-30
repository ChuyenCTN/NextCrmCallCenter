package org.linphone.activities.main.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.StatusFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.GenericFragment
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.activities.main.viewmodels.StatusViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.Event

class StatusFragment : GenericFragment<StatusFragmentBinding>() {
    private lateinit var viewModel: StatusViewModel
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getLayoutId(): Int = R.layout.status_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        useMaterialSharedAxisXForwardAnimation = false

        viewModel = ViewModelProvider(this).get(StatusViewModel::class.java)
        binding.viewModel = viewModel

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        sharedViewModel.accountRemoved.observe(
            viewLifecycleOwner,
            {
                Log.i("[Status Fragment] An account was removed, update default account state")
                val defaultAccount = coreContext.core.defaultAccount
                if (defaultAccount != null) {
                    viewModel.updateDefaultAccountRegistrationStatus(defaultAccount.state)
                }
            }
        )

        binding.setMenuClickListener {
            sharedViewModel.toggleDrawerEvent.value = Event(true)
        }

        binding.setRefreshClickListener {
            viewModel.refreshRegister()
        }
    }
}
