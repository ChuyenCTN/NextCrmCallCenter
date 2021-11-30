package org.linphone.activities.main.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AboutFragmentBinding
import org.linphone.activities.main.fragments.SecureFragment

class AboutFragment : SecureFragment<AboutFragmentBinding>() {
    private lateinit var viewModel: AboutViewModel

    override fun getLayoutId(): Int = R.layout.about_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this).get(AboutViewModel::class.java)
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }

        binding.setPrivacyPolicyClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.about_privacy_policy_link))
            )
            startActivity(browserIntent)
        }

        binding.setLicenseClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.about_license_link))
            )
            startActivity(browserIntent)
        }
    }
}
