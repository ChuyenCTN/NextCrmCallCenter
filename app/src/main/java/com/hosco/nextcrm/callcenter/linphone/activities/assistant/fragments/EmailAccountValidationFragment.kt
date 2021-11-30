package org.linphone.activities.assistant.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantEmailAccountValidationFragmentBinding
import org.linphone.activities.GenericFragment
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.assistant.viewmodels.EmailAccountValidationViewModel
import org.linphone.activities.assistant.viewmodels.EmailAccountValidationViewModelFactory
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.navigateToAccountLinking

class EmailAccountValidationFragment : GenericFragment<AssistantEmailAccountValidationFragmentBinding>() {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    private lateinit var viewModel: EmailAccountValidationViewModel

    override fun getLayoutId(): Int = R.layout.assistant_email_account_validation_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
        }

        viewModel = ViewModelProvider(this, EmailAccountValidationViewModelFactory(sharedViewModel.getAccountCreator())).get(EmailAccountValidationViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.leaveAssistantEvent.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    coreContext.contactsManager.updateLocalContacts()

                    val args = Bundle()
                    args.putBoolean("AllowSkip", true)
                    args.putString("Username", viewModel.accountCreator.username)
                    args.putString("Password", viewModel.accountCreator.password)
                    navigateToAccountLinking(args)
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
