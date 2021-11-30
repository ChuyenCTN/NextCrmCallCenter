package org.linphone.activities.main.settings.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.BR
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SettingsVideoFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.main.settings.SettingListenerStub
import org.linphone.activities.main.settings.viewmodels.VideoSettingsViewModel
import org.linphone.activities.navigateToEmptySetting
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.PermissionHelper

class VideoSettingsFragment : GenericSettingFragment<SettingsVideoFragmentBinding>() {
    private lateinit var viewModel: VideoSettingsViewModel

    override fun getLayoutId(): Int = R.layout.settings_video_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.sharedMainViewModel = sharedViewModel

        viewModel = ViewModelProvider(this).get(VideoSettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.setBackClickListener { goBack() }

        initVideoCodecsList()

        if (!PermissionHelper.required(requireContext()).hasCameraPermission()) {
            Log.i("[Video Settings] Asking for CAMERA permission")
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val granted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (granted) {
            Log.i("[Video Settings] CAMERA permission granted")
            coreContext.core.reloadVideoDevices()
            viewModel.initCameraDevicesList()
        } else {
            Log.w("[Video Settings] CAMERA permission denied")
        }
    }

    private fun initVideoCodecsList() {
        val list = arrayListOf<ViewDataBinding>()
        for (payload in coreContext.core.videoPayloadTypes) {
            val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(requireContext()), R.layout.settings_widget_switch_and_text, null, false)
            binding.setVariable(BR.switch_title, payload.mimeType)
            binding.setVariable(BR.switch_subtitle, "")
            binding.setVariable(BR.text_title, "recv-fmtp")
            binding.setVariable(BR.text_subtitle, "")
            binding.setVariable(BR.defaultValue, payload.recvFmtp)
            binding.setVariable(BR.checked, payload.enabled())
            binding.setVariable(
                BR.listener,
                object : SettingListenerStub() {
                    override fun onBoolValueChanged(newValue: Boolean) {
                        payload.enable(newValue)
                    }

                    override fun onTextValueChanged(newValue: String) {
                        payload.recvFmtp = newValue
                    }
                }
            )
            binding.lifecycleOwner = viewLifecycleOwner
            list.add(binding)
        }
        viewModel.videoCodecs.value = list
    }

    override fun goBack() {
        if (sharedViewModel.isSlidingPaneSlideable.value == true) {
            sharedViewModel.closeSlidingPaneEvent.value = Event(true)
        } else {
            navigateToEmptySetting()
        }
    }
}
