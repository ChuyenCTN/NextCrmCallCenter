package org.linphone.activities.call.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.CallVideoFragmentBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.GenericFragment
import org.linphone.activities.call.VideoZoomHelper
import org.linphone.activities.call.viewmodels.CallsViewModel
import org.linphone.activities.call.viewmodels.ConferenceViewModel
import org.linphone.activities.call.viewmodels.ControlsFadingViewModel

class VideoRenderingFragment : GenericFragment<CallVideoFragmentBinding>() {
    private lateinit var controlsFadingViewModel: ControlsFadingViewModel
    private lateinit var callsViewModel: CallsViewModel
    private lateinit var conferenceViewModel: ConferenceViewModel

    private var previewX: Float = 0f
    private var previewY: Float = 0f
    private lateinit var videoZoomHelper: VideoZoomHelper

    override fun getLayoutId(): Int = R.layout.call_video_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this

        controlsFadingViewModel = requireActivity().run {
            ViewModelProvider(this).get(ControlsFadingViewModel::class.java)
        }
        binding.controlsFadingViewModel = controlsFadingViewModel

        callsViewModel = requireActivity().run {
            ViewModelProvider(this).get(CallsViewModel::class.java)
        }

        conferenceViewModel = requireActivity().run {
            ViewModelProvider(this).get(ConferenceViewModel::class.java)
        }
        binding.conferenceViewModel = conferenceViewModel

        coreContext.core.nativeVideoWindowId = binding.remoteVideoSurface
        coreContext.core.nativePreviewWindowId = binding.localPreviewVideoSurface

        binding.setPreviewTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    previewX = v.x - event.rawX
                    previewY = v.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    v.animate()
                        .x(event.rawX + previewX)
                        .y(event.rawY + previewY)
                        .setDuration(0)
                        .start()
                }
                else -> {
                    v.performClick()
                    false
                }
            }
            true
        }

        videoZoomHelper = VideoZoomHelper(requireContext(), binding.remoteVideoSurface)
    }
}
