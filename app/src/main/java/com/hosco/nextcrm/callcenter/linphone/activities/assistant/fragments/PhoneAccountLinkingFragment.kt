package org.linphone.activities.assistant.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantPhoneAccountLinkingFragmentBinding
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.assistant.viewmodels.PhoneAccountLinkingViewModel
import org.linphone.activities.assistant.viewmodels.PhoneAccountLinkingViewModelFactory
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.navigateToEchoCancellerCalibration
import org.linphone.activities.navigateToPhoneAccountValidation
import org.linphone.core.tools.Log

class PhoneAccountLinkingFragment : AbstractPhoneFragment<AssistantPhoneAccountLinkingFragmentBinding>() {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    override lateinit var viewModel: PhoneAccountLinkingViewModel

    override fun getLayoutId(): Int = R.layout.assistant_phone_account_linking_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
        }

        val accountCreator = sharedViewModel.getAccountCreator()
        viewModel = ViewModelProvider(this, PhoneAccountLinkingViewModelFactory(accountCreator)).get(PhoneAccountLinkingViewModel::class.java)
        binding.viewModel = viewModel

        val username = arguments?.getString("Username")
        Log.i("[Phone Account Linking] username to link is $username")
        viewModel.username.value = username

        val password = arguments?.getString("Password")
        accountCreator.password = password

        val ha1 = arguments?.getString("HA1")
        accountCreator.ha1 = ha1

        val allowSkip = arguments?.getBoolean("AllowSkip", false)
        viewModel.allowSkip.value = allowSkip

        binding.setInfoClickListener {
            showPhoneNumberInfoDialog()
        }

        binding.setSelectCountryClickListener {
            CountryPickerFragment(viewModel).show(childFragmentManager, "CountryPicker")
        }

        viewModel.goToSmsValidationEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    val args = Bundle()
                    args.putBoolean("IsLinking", true)
                    args.putString("PhoneNumber", viewModel.accountCreator.phoneNumber)
                    navigateToPhoneAccountValidation(args)
                }
            }
        )

        viewModel.leaveAssistantEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    if (coreContext.core.isEchoCancellerCalibrationRequired) {
                        navigateToEchoCancellerCalibration()
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        )

        viewModel.onErrorEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { message ->
                    (requireActivity() as AssistantActivity).showSnackBar(message)
                }
            }
        )

        checkPermission()
    }
}
