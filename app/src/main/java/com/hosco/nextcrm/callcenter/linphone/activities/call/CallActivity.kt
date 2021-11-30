package org.linphone.activities.call

import android.Manifest
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.window.layout.FoldingFeature
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.databinding.CallActivityCrmBinding
import com.hosco.nextcrm.callcenter.extension.addCharacter
import com.hosco.nextcrm.callcenter.model.response.DeviceContact
import com.hosco.nextcrm.callcenter.ui.addnote.AddCallNoteActivity
import com.hosco.nextcrm.callcenter.utils.GenColorBackground
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_dialpad_my.*
import kotlinx.android.synthetic.main.call_outgoing_activity.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.linphone.activities.call.viewmodels.*
import org.linphone.activities.main.MainActivity
import org.linphone.activities.main.viewmodels.DialogViewModel
import org.linphone.compatibility.Compatibility
import org.linphone.core.Call
import org.linphone.core.tools.Log
import org.linphone.mediastream.Version
import org.linphone.utils.AppUtils
import org.linphone.utils.DialogUtils
import org.linphone.utils.Event
import org.linphone.utils.PermissionHelper

class CallActivity : ProximitySensorActivity() {
    private lateinit var binding: CallActivityCrmBinding
    private lateinit var viewModel: ControlsFadingViewModel
    private lateinit var sharedViewModel: SharedCallViewModel

    private lateinit var callsViewModel: CallsViewModel
    private lateinit var controlsViewModel: ControlsViewModel
    private lateinit var conferenceViewModel: ConferenceViewModel

    private var foldingFeature: FoldingFeature? = null

    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Compatibility.setShowWhenLocked(this, true)
        Compatibility.setTurnScreenOn(this, true)

        binding = DataBindingUtil.setContentView(this, R.layout.call_activity_crm)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(ControlsFadingViewModel::class.java)
        binding.controlsFadingViewModel = viewModel

        sharedViewModel = ViewModelProvider(this).get(SharedCallViewModel::class.java)

        sharedViewModel.resetHiddenInterfaceTimerInVideoCallEvent.observe(
            this,
            {
                it.consume {
                    viewModel.showMomentarily()
                }
            }
        )

        viewModel.proximitySensorEnabled.observe(
            this,
            {
                enableProximitySensor(it)
            }
        )

        viewModel.videoEnabled.observe(
            this,
            {
                updateConstraintSetDependingOnFoldingState()
            }
        )

        callsViewModel = run {
            ViewModelProvider(this).get(CallsViewModel::class.java)
        }
        binding.viewModel = callsViewModel

        controlsViewModel = run {
            ViewModelProvider(this).get(ControlsViewModel::class.java)
        }

        binding.controlsViewModel = controlsViewModel

        conferenceViewModel = run {
            ViewModelProvider(this).get(ConferenceViewModel::class.java)
        }
        binding.conferenceViewModel = conferenceViewModel

        callsViewModel.currentCallViewModel.observe(
            this,
            {
                if (it != null) {
                    binding.callDurationText.base =
                        SystemClock.elapsedRealtime() - (1000 * it.call.duration) // Linphone timestamps are in seconds
                    binding.callDurationText.start()
                }
            }
        )

        callsViewModel.noMoreCallEvent.observe(
            this,
            {
                it.consume {
                    finish()
                }
            }
        )

        callsViewModel.askWriteExternalStoragePermissionEvent.observe(
            this,
            {
                it.consume {
                    if (!PermissionHelper.get().hasWriteExternalStorage()) {
                        Log.i("[Controls Fragment] Asking for WRITE_EXTERNAL_STORAGE permission")
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                    }
                }
            }
        )

        callsViewModel.callUpdateEvent.observe(
            this,
            {
                it.consume { call ->
                    if (call.state == Call.State.StreamsRunning) {
                        dialog?.dismiss()
                    } else if (call.state == Call.State.UpdatedByRemote) {
                        if (coreContext.core.videoCaptureEnabled() || coreContext.core.videoDisplayEnabled()) {
                            if (call.currentParams.videoEnabled() != call.remoteParams?.videoEnabled()) {
                                showCallVideoUpdateDialog(call)
                            }
                        } else {
                            Log.w("[Controls Fragment] Video display & capture are disabled, don't show video dialog")
                        }
                    }
                }
            }
        )

        controlsViewModel.chatClickedEvent.observe(
            this,
            {
                it.consume {
                    val intent = Intent()
                    intent.setClass(this, MainActivity::class.java)
                    intent.putExtra("Chat", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        )

        controlsViewModel.addCallClickedEvent.observe(
            this,
            {
                it.consume {
                    val intent = Intent()
                    intent.setClass(this, MainActivity::class.java)
                    intent.putExtra("Dialer", true)
                    intent.putExtra("Transfer", false)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        )

        controlsViewModel.transferCallClickedEvent.observe(
            this,
            {
                it.consume {
                    val intent = Intent()
                    intent.setClass(this, MainActivity::class.java)
                    intent.putExtra("Dialer", true)
                    intent.putExtra("Transfer", true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        )

        controlsViewModel.askPermissionEvent.observe(
            this,
            {
                it.consume { permission ->
                    Log.i("[Controls Fragment] Asking for $permission permission")
                    requestPermissions(arrayOf(permission), 0)
                }
            }
        )

        controlsViewModel.toggleNumpadEvent.observe(
            this,
            {
                it.consume { open ->
//                    if (this::numpadAnimator.isInitialized) {
//                        if (open) {
//                            numpadAnimator.start()
//                        } else {
//                            numpadAnimator.reverse()
//                        }
//                    }
                }
            }
        )

        controlsViewModel.somethingClickedEvent.observe(
            this,
            {
                it.consume {
                    sharedViewModel.resetHiddenInterfaceTimerInVideoCallEvent.value = Event(true)
                }
            }
        )

        if (Version.sdkAboveOrEqual(Version.API23_MARSHMALLOW_60)) {
            checkPermissions()
        }

        controlsViewModel.isSpeakerSelected.observe(this, {
            if (it)
                call_toggle_speaker.setImageDrawable(getDrawable(R.drawable.ic_speaker_on_vector))
            else call_toggle_speaker.setImageDrawable(getDrawable(R.drawable.ic_speaker_off_vector))
        })

        controlsViewModel.isMicrophoneMuted.observe(this, {
            if (it)
                call_toggle_microphone.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic_off))
            else call_toggle_microphone.setImageDrawable(getDrawable(R.drawable.ic_baseline_mic))
        })

        handleDialpad()

        callsViewModel.callState.observe(this, Observer {
            Log.i("[State CRM] for ${it}")
            if (it.equals("Released")) {
                coreContext.core.nativeVideoWindowId = null
                coreContext.core.nativePreviewWindowId = null
                val intent = Intent(this, AddCallNoteActivity::class.java)
                intent.putExtra(
                    Const.DATA_PHONE,
                    callsViewModel.currentCallViewModel.value?.contact?.value?.fullName
                )
                startActivity(intent)
            }
        })

    }


    override fun onLayoutChanges(foldingFeature: FoldingFeature?) {
        this.foldingFeature = foldingFeature
        updateConstraintSetDependingOnFoldingState()
    }

    override fun onResume() {
        super.onResume()

        if (coreContext.core.callsNb == 0) {
            Log.w("[Call Activity] Resuming but no call found...")
            if (isTaskRoot) {
                // When resuming app from recent tasks make sure MainActivity will be launched if there is no call
                val intent = Intent()
                intent.setClass(this, CallActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                finish()
            }
        } else {
            coreContext.removeCallOverlay()
        }

        if (corePreferences.fullScreenCallUI) {
            hideSystemUI()
            window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
                if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                    GlobalScope.launch {
                        delay(2000)
                        withContext(Dispatchers.Main) {
                            hideSystemUI()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        val core = coreContext.core
        if (core.callsNb > 0) {
            coreContext.createCallOverlay()
        }

        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (coreContext.isVideoCallOrConferenceActive()) {
            Compatibility.enterPipMode(this)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (isInPictureInPictureMode) {
            viewModel.areControlsHidden.value = true
        }

        if (corePreferences.hideCameraPreviewInPipMode) {
            viewModel.isVideoPreviewHidden.value = isInPictureInPictureMode
        } else {
            viewModel.isVideoPreviewResizedForPip.value = isInPictureInPictureMode
        }
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        if (corePreferences.fullScreenCallUI) {
            theme.applyStyle(R.style.FullScreenTheme, true)
        }
        return theme
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    private fun updateConstraintSetDependingOnFoldingState() {
        val feature = foldingFeature ?: return
//        val constraintLayout = binding.constraintLayout
//        val set = ConstraintSet()
//        set.clone(constraintLayout)
//
//        if (feature.state == FoldingFeature.State.HALF_OPENED && viewModel.videoEnabled.value == true) {
//            set.setGuidelinePercent(R.id.hinge_top, 0.5f)
//            set.setGuidelinePercent(R.id.hinge_bottom, 0.5f)
//            viewModel.disable(true)
//        } else {
//            set.setGuidelinePercent(R.id.hinge_top, 0f)
//            set.setGuidelinePercent(R.id.hinge_bottom, 1f)
//            viewModel.disable(false)
//        }
//
//        set.applyTo(constraintLayout)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            for (i in permissions.indices) {
                when (permissions[i]) {
                    Manifest.permission.RECORD_AUDIO -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.i("[Controls Fragment] RECORD_AUDIO permission has been granted")
                        controlsViewModel.updateMuteMicState()
                    }
                    Manifest.permission.CAMERA -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.i("[Controls Fragment] CAMERA permission has been granted")
                        coreContext.core.reloadVideoDevices()
                    }
                }
            }
        } else if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callsViewModel.takeScreenshot()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @TargetApi(Version.API23_MARSHMALLOW_60)
    private fun checkPermissions() {
        val permissionsRequiredList = arrayListOf<String>()

        if (!PermissionHelper.get().hasRecordAudioPermission()) {
            Log.i("[Controls Fragment] Asking for RECORD_AUDIO permission")
            permissionsRequiredList.add(Manifest.permission.RECORD_AUDIO)
        }

        if (coreContext.isVideoCallOrConferenceActive() && !PermissionHelper.get()
                .hasCameraPermission()
        ) {
            Log.i("[Controls Fragment] Asking for CAMERA permission")
            permissionsRequiredList.add(Manifest.permission.CAMERA)
        }

        if (permissionsRequiredList.isNotEmpty()) {
            val permissionsRequired = arrayOfNulls<String>(permissionsRequiredList.size)
            permissionsRequiredList.toArray(permissionsRequired)
            requestPermissions(permissionsRequired, 0)
        }
    }

    private fun showCallVideoUpdateDialog(call: Call) {
        val viewModel =
            DialogViewModel(AppUtils.getString(R.string.call_video_update_requested_dialog))
        dialog = DialogUtils.getDialog(this, viewModel)

        viewModel.showCancelButton(
            {
                callsViewModel.answerCallVideoUpdateRequest(call, false)
                dialog?.dismiss()
            },
            getString(R.string.dialog_decline)
        )

        viewModel.showOkButton(
            {
                callsViewModel.answerCallVideoUpdateRequest(call, true)
                dialog?.dismiss()
            },
            getString(R.string.dialog_accept)
        )

        dialog?.show()
    }

    fun loadTextAvatar() {
        try {
            if (!callsViewModel.currentCallViewModel.value?.contact?.value?.fullName?.isNullOrEmpty()!!) {
                loadTextBoder(
                    caller_avatar,
                    callsViewModel.currentCallViewModel.value?.contact?.value?.fullName.toString()
                )
            } else
                callsViewModel.currentCallViewModel.value!!.displayName.value?.let {
                    loadTextBoder(
                        caller_avatar,
                        it
                    )
                }
        } catch (e: Exception) {
        }
    }

    fun loadTextBoder(view: TextView, text: String) {
        text.let {
            if (text.isNotEmpty())
                if (text.length >= 2) {
                    if (text.contains(" "))
                        view.text = text.substring(0, 1).plus(text.split(" ")[1].substring(0, 1))
                    else
                        view.text =
                            text.substring(0, 1)
                                .plus(text.substring(text.length - 2, text.length - 1))
                } else {
                    view.text = text.substring(0, 1)
                }
            view.background = GenColorBackground.getBackgroundWithBorder()
        }
    }

    fun handleDialpad() {
        PushDownAnim.setPushDownAnimTo(binding.callDialpad).setOnClickListener {
            binding.dialpadWrapper.visibility = View.VISIBLE
        }
        PushDownAnim.setPushDownAnimTo(binding.dialpadClose).setOnClickListener {
            binding.dialpadWrapper.visibility = View.GONE
        }
        eventClickKeyoard()
    }

    private fun disableKeyboardPopping() {
        binding.dialpadInclude.edValueDialpad.showSoftInputOnFocus = false
    }

    fun eventClickKeyoard() {
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn0)
            .setOnClickListener {
                dialpadPressed('0', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn1)
            .setOnClickListener {
                dialpadPressed('1', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn2)
            .setOnClickListener {
                dialpadPressed('2', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn3)
            .setOnClickListener {
                dialpadPressed('3', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn4)
            .setOnClickListener {
                dialpadPressed('4', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn5)
            .setOnClickListener {
                dialpadPressed('5', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn6)
            .setOnClickListener {
                dialpadPressed('6', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn7)
            .setOnClickListener {
                dialpadPressed('7', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn8)
            .setOnClickListener {
                dialpadPressed('8', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btn9)
            .setOnClickListener {
                dialpadPressed('9', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btnstar)
            .setOnClickListener {
                dialpadPressed('*', it)
            }
        PushDownAnim.setPushDownAnimTo(binding.dialpadInclude.btnhash)
            .setOnClickListener {
                dialpadPressed('#', it)
            }

        disableKeyboardPopping()
    }

    private fun dialpadPressed(char: Char, view: View?) {
        binding.dialpadInclude.edValueDialpad.addCharacter(char)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: DeviceContact) {
        if (callsViewModel.isShowNumberPhone.value == false)
            callsViewModel.setDeviceContact(result)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

}
