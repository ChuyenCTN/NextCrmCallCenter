package org.linphone.activities.assistant.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantEchoCancellerCalibrationFragmentBinding
import org.linphone.activities.GenericFragment
import org.linphone.activities.assistant.viewmodels.EchoCancellerCalibrationViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.PermissionHelper

class EchoCancellerCalibrationFragment : GenericFragment<AssistantEchoCancellerCalibrationFragmentBinding>() {
    private lateinit var viewModel: EchoCancellerCalibrationViewModel

    override fun getLayoutId(): Int = R.layout.assistant_echo_canceller_calibration_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this).get(EchoCancellerCalibrationViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.echoCalibrationTerminated.observe(
            viewLifecycleOwner,
            {
                it.consume {
                    requireActivity().finish()
                }
            }
        )

        if (!PermissionHelper.required(requireContext()).hasRecordAudioPermission()) {
            Log.i("[Echo Canceller Calibration] Asking for RECORD_AUDIO permission")
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 0)
        } else {
            viewModel.startEchoCancellerCalibration()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (granted) {
            Log.i("[Echo Canceller Calibration] RECORD_AUDIO permission granted")
            viewModel.startEchoCancellerCalibration()
        } else {
            Log.w("[Echo Canceller Calibration] RECORD_AUDIO permission denied")
            requireActivity().finish()
        }
    }
}
