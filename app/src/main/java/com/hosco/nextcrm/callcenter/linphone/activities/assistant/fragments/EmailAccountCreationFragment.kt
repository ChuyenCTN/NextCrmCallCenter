package org.linphone.activities.assistant.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantEmailAccountCreationFragmentBinding
import org.linphone.activities.GenericFragment
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.assistant.viewmodels.EmailAccountCreationViewModel
import org.linphone.activities.assistant.viewmodels.EmailAccountCreationViewModelFactory
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.navigateToEmailAccountValidation

class EmailAccountCreationFragment : GenericFragment<AssistantEmailAccountCreationFragmentBinding>() {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    private lateinit var viewModel: EmailAccountCreationViewModel

    override fun getLayoutId(): Int = R.layout.assistant_email_account_creation_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
        }

        viewModel = ViewModelProvider(this, EmailAccountCreationViewModelFactory(sharedViewModel.getAccountCreator())).get(EmailAccountCreationViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.goToEmailValidationEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    navigateToEmailAccountValidation()
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
