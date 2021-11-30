package org.linphone.activities.main.dialer.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.FileConfigViewerFragmentBinding
import org.linphone.activities.main.dialer.viewmodels.ConfigFileViewModel
import org.linphone.activities.main.fragments.SecureFragment
import org.linphone.core.tools.Log

class ConfigViewerFragment : SecureFragment<FileConfigViewerFragmentBinding>() {
    private lateinit var viewModel: ConfigFileViewModel

    override fun getLayoutId(): Int = R.layout.file_config_viewer_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[ConfigFileViewModel::class.java]
        binding.viewModel = viewModel

        isSecure = arguments?.getBoolean("Secure") ?: false

        binding.setBackClickListener {
            goBack()
        }

        binding.setExportClickListener {
            shareConfig()
        }
    }

    private fun shareConfig() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, viewModel.text.value.orEmpty())
        intent.type = "text/plain"

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)))
        } catch (ex: ActivityNotFoundException) {
            Log.e(ex)
        }
    }
}
