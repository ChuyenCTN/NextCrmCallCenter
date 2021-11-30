package org.linphone.activities.assistant.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantAccountLoginFragmentBinding
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.assistant.viewmodels.AccountLoginViewModel
import org.linphone.activities.assistant.viewmodels.AccountLoginViewModelFactory
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.main.viewmodels.DialogViewModel
import org.linphone.activities.navigateToEchoCancellerCalibration
import org.linphone.activities.navigateToPhoneAccountValidation
import org.linphone.utils.DialogUtils

class AccountLoginFragment : AbstractPhoneFragment<AssistantAccountLoginFragmentBinding>() {
    override lateinit var viewModel: AccountLoginViewModel
    private lateinit var sharedViewModel: SharedAssistantViewModel

    override fun getLayoutId(): Int = R.layout.assistant_account_login_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
        }

        viewModel = ViewModelProvider(this, AccountLoginViewModelFactory(sharedViewModel.getAccountCreator())).get(AccountLoginViewModel::class.java)
        binding.viewModel = viewModel

        if (resources.getBoolean(R.bool.isTablet)) {
            viewModel.loginWithUsernamePassword.value = true
        }

        binding.setInfoClickListener {
            showPhoneNumberInfoDialog()
        }

        binding.setSelectCountryClickListener {
            CountryPickerFragment(viewModel).show(childFragmentManager, "CountryPicker")
        }

        binding.setForgotPasswordClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(getString(R.string.assistant_forgotten_password_link))
            startActivity(intent)
        }

        viewModel.goToSmsValidationEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    val args = Bundle()
                    args.putBoolean("IsLogin", true)
                    args.putString("PhoneNumber", viewModel.accountCreator.phoneNumber)
                    navigateToPhoneAccountValidation(args)
                }
            }
        )

        viewModel.leaveAssistantEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    coreContext.contactsManager.updateLocalContacts()

                    if (coreContext.core.isEchoCancellerCalibrationRequired) {
                        navigateToEchoCancellerCalibration()
                    } else {
                        requireActivity().finish()
                    }
                }
            }
        )

        viewModel.invalidCredentialsEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    val dialogViewModel = DialogViewModel(getString(R.string.assistant_error_invalid_credentials))
                    val dialog: Dialog = DialogUtils.getDialog(requireContext(), dialogViewModel)

                    dialogViewModel.showCancelButton {
                        viewModel.removeInvalidProxyConfig()
                        dialog.dismiss()
                    }

                    dialogViewModel.showDeleteButton(
                        {
                            viewModel.continueEvenIfInvalidCredentials()
                            dialog.dismiss()
                        },
                        getString(R.string.assistant_continue_even_if_credentials_invalid)
                    )

                    dialog.show()
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
