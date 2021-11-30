package org.linphone.activities.assistant.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantPhoneAccountCreationFragmentBinding
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.assistant.viewmodels.PhoneAccountCreationViewModel
import org.linphone.activities.assistant.viewmodels.PhoneAccountCreationViewModelFactory
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.navigateToPhoneAccountValidation

class PhoneAccountCreationFragment : AbstractPhoneFragment<AssistantPhoneAccountCreationFragmentBinding>() {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    override lateinit var viewModel: PhoneAccountCreationViewModel

    override fun getLayoutId(): Int = R.layout.assistant_phone_account_creation_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
        }

        viewModel = ViewModelProvider(this, PhoneAccountCreationViewModelFactory(sharedViewModel.getAccountCreator())).get(PhoneAccountCreationViewModel::class.java)
        binding.viewModel = viewModel

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
                    args.putBoolean("IsCreation", true)
                    args.putString("PhoneNumber", viewModel.accountCreator.phoneNumber)
                    navigateToPhoneAccountValidation(args)
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
