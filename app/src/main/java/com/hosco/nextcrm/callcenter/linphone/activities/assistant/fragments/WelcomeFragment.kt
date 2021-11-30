package org.linphone.activities.assistant.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantWelcomeFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.*
import org.linphone.activities.assistant.viewmodels.WelcomeViewModel
import java.util.regex.Pattern

class WelcomeFragment : GenericFragment<AssistantWelcomeFragmentBinding>() {
    private lateinit var viewModel: WelcomeViewModel

    override fun getLayoutId(): Int = R.layout.assistant_welcome_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this).get(WelcomeViewModel::class.java)
        binding.viewModel = viewModel

        binding.setCreateAccountClickListener {
            if (resources.getBoolean(R.bool.isTablet)) {
                navigateToEmailAccountCreation()
            } else {
                navigateToPhoneAccountCreation()
            }
        }

        binding.setAccountLoginClickListener {
            navigateToAccountLogin()
        }

        binding.setGenericAccountLoginClickListener {
            navigateToGenericLogin()
        }

        binding.setRemoteProvisioningClickListener {
            navigateToRemoteProvisioning()
        }

        viewModel.termsAndPrivacyAccepted.observe(
            viewLifecycleOwner,
            {
                if (it) corePreferences.readAndAgreeTermsAndPrivacy = true
            }
        )

        setUpTermsAndPrivacyLinks()
    }

    private fun setUpTermsAndPrivacyLinks() {
        val terms = getString(R.string.assistant_general_terms)
        val privacy = getString(R.string.assistant_privacy_policy)

        val label = getString(
            R.string.assistant_read_and_agree_terms,
            terms,
            privacy
        )
        val spannable = SpannableString(label)

        val termsMatcher = Pattern.compile(terms).matcher(label)
        if (termsMatcher.find()) {
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.assistant_general_terms_link))
                    )
                    startActivity(browserIntent)
                }
            }
            spannable.setSpan(clickableSpan, termsMatcher.start(0), termsMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val policyMatcher = Pattern.compile(privacy).matcher(label)
        if (policyMatcher.find()) {
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.assistant_privacy_policy_link))
                    )
                    startActivity(browserIntent)
                }
            }
            spannable.setSpan(clickableSpan, policyMatcher.start(0), policyMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.termsAndPrivacy.text = spannable
        binding.termsAndPrivacy.movementMethod = LinkMovementMethod.getInstance()
    }
}
