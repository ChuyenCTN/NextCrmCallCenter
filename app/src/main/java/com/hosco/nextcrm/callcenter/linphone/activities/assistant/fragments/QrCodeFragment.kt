package org.linphone.activities.assistant.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantQrCodeFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.GenericFragment
import org.linphone.activities.assistant.viewmodels.QrCodeViewModel
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.PermissionHelper

class QrCodeFragment : GenericFragment<AssistantQrCodeFragmentBinding>() {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    private lateinit var viewModel: QrCodeViewModel

    override fun getLayoutId(): Int = R.layout.assistant_qr_code_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
        }

        viewModel = ViewModelProvider(this).get(QrCodeViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.qrCodeFoundEvent.observe(
            viewLifecycleOwner,
            {
                it.consume { url ->
                    sharedViewModel.remoteProvisioningUrl.value = url
                    findNavController().navigateUp()
                }
            }
        )
        viewModel.setBackCamera()

        if (!PermissionHelper.required(requireContext()).hasCameraPermission()) {
            Log.i("[QR Code] Asking for CAMERA permission")
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 0)
        }
    }

    override fun onResume() {
        super.onResume()

        coreContext.core.nativePreviewWindowId = binding.qrCodeCaptureTexture
        coreContext.core.enableQrcodeVideoPreview(true)
        coreContext.core.enableVideoPreview(true)
    }

    override fun onPause() {
        coreContext.core.nativePreviewWindowId = null
        coreContext.core.enableQrcodeVideoPreview(false)
        coreContext.core.enableVideoPreview(false)

        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (granted) {
            Log.i("[QR Code] CAMERA permission granted")
            coreContext.core.reloadVideoDevices()
            viewModel.setBackCamera()
        } else {
            Log.w("[QR Code] CAMERA permission denied")
            findNavController().navigateUp()
        }
    }
}
