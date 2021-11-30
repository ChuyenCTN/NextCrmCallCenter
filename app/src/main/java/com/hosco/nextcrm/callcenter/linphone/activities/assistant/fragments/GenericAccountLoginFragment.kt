package org.linphone.activities.assistant.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantGenericAccountLoginFragmentBinding
import org.linphone.activities.GenericFragment
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.assistant.viewmodels.GenericLoginViewModel
import org.linphone.activities.assistant.viewmodels.GenericLoginViewModelFactory
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.main.viewmodels.DialogViewModel
import org.linphone.activities.navigateToEchoCancellerCalibration
import org.linphone.utils.DialogUtils

class GenericAccountLoginFragment : GenericFragment<AssistantGenericAccountLoginFragmentBinding>() {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    private lateinit var viewModel: GenericLoginViewModel

    override fun getLayoutId(): Int = R.layout.assistant_generic_account_login_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
        }

        viewModel = ViewModelProvider(this, GenericLoginViewModelFactory(sharedViewModel.getAccountCreator(true))).get(GenericLoginViewModel::class.java)
        binding.viewModel = viewModel

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
    }
}
