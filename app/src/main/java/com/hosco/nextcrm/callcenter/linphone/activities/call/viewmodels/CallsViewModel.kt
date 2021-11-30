package org.linphone.activities.call.viewmodels

import android.annotation.SuppressLint
import android.provider.CallLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.common.extensions.AllContactList
import com.hosco.nextcrm.callcenter.model.response.DeviceContact
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import org.linphone.core.*
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.LinphoneUtils
import org.linphone.utils.PermissionHelper

@SuppressLint("NullSafeMutableLiveData")
class CallsViewModel : ViewModel() {
    val currentCallViewModel = MutableLiveData<CallViewModel>()

    val noActiveCall = MutableLiveData<Boolean>()

    val callPausedByRemote = MutableLiveData<Boolean>()

    val address: String by lazy {
        SharePreferenceUtils.getInstances().getAuthResponse().user.extentionConfig.displayName
    }

    val pausedCalls = MutableLiveData<ArrayList<CallViewModel>>()

    val noMoreCallEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }
    val callState: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val callUpdateEvent: MutableLiveData<Event<Call>> by lazy {
        MutableLiveData<Event<Call>>()
    }

    val askWriteExternalStoragePermissionEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    private val _nameContact = MutableLiveData<String>()
    val nameContact: LiveData<String> = _nameContact

    private val _phoneContact = MutableLiveData<String>()
    val phoneContact: LiveData<String> = _phoneContact

    private val _isShowNumberPhone = MutableLiveData<Boolean>()
    val isShowNumberPhone: LiveData<Boolean> = _isShowNumberPhone

    fun setDeviceContact(deviceContact: DeviceContact) {
        _nameContact.value = deviceContact.name
        _phoneContact.value = deviceContact.phone
    }

    private val listener = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State,
            message: String
        ) {
            Log.i("[Calls VM] Call state changed: $state")
            callState.postValue(state.toString())
            callPausedByRemote.value =
                (state == Call.State.PausedByRemote) and (call.conference == null)

            val currentCall = core.currentCall
            noActiveCall.value = currentCall == null
            if (currentCall == null) {
                currentCallViewModel.value?.destroy()
            } else if (currentCallViewModel.value?.call != currentCall) {
                val viewModel = CallViewModel(currentCall)
                currentCallViewModel.value = viewModel
            }

            if (state == Call.State.End || state == Call.State.Released || state == Call.State.Error) {
                if (core.callsNb == 0) {
                    noMoreCallEvent.value = Event(true)
                } else {
                    removeCallFromPausedListIfPresent(call)
                }
            } else if (state == Call.State.Paused) {
                addCallToPausedList(call)
            } else if (state == Call.State.Resuming) {
                removeCallFromPausedListIfPresent(call)
            } else if (call.state == Call.State.UpdatedByRemote) {
                // If the correspondent asks to turn on video while audio call,
                // defer update until user has chosen whether to accept it or not
                val remoteVideo = call.remoteParams?.videoEnabled() ?: false
                val localVideo = call.currentParams.videoEnabled()
                val autoAccept = call.core.videoActivationPolicy.automaticallyAccept
                if (remoteVideo && !localVideo && !autoAccept) {
                    if (coreContext.core.videoCaptureEnabled() || coreContext.core.videoDisplayEnabled()) {
                        call.deferUpdate()
                        callUpdateEvent.value = Event(call)
                    } else {
                        coreContext.answerCallVideoUpdateRequest(call, false)
                    }
                }
            } else if (state == Call.State.StreamsRunning) {
                callUpdateEvent.value = Event(call)
            }
        }
    }

    init {
        coreContext.core.addListener(listener)

//        callState.value= coreContext.context.resources.getString(R.string.is_calling)

        val currentCall = coreContext.core.currentCall

        noActiveCall.value = currentCall == null
        if (currentCall != null) {
            currentCallViewModel.value?.destroy()

            val viewModel = CallViewModel(currentCall)
            currentCallViewModel.value = viewModel
        }

        callPausedByRemote.value = currentCall?.state == Call.State.PausedByRemote

        for (call in coreContext.core.calls) {
            if (call.state == Call.State.Paused || call.state == Call.State.Pausing) {
                addCallToPausedList(call)
            }
        }

        val deviceContact =
            AllContactList.getInstance().getContact(AllContactList.getInstance().temporaryPhone)
        if (deviceContact != null) {
            _nameContact.value = deviceContact.name
            _phoneContact.value = " - ${deviceContact.phone}"
            _isShowNumberPhone.value = true
        } else {
            _nameContact.value = AllContactList.getInstance().temporaryPhone
            _isShowNumberPhone.value = false
        }
    }

    override fun onCleared() {
        coreContext.core.removeListener(listener)

        super.onCleared()
    }

    fun answerCallVideoUpdateRequest(call: Call, accept: Boolean) {
        coreContext.answerCallVideoUpdateRequest(call, accept)
    }

    fun takeScreenshot() {
        if (!PermissionHelper.get().hasWriteExternalStorage()) {
            askWriteExternalStoragePermissionEvent.value = Event(true)
        } else {
            currentCallViewModel.value?.takeScreenshot()
        }
    }

    private fun addCallToPausedList(call: Call) {
        if (call.conference != null) return // Conference will be displayed as paused, no need to display the call as well

        val list = arrayListOf<CallViewModel>()
        list.addAll(pausedCalls.value.orEmpty())

        for (pausedCallViewModel in list) {
            if (pausedCallViewModel.call == call) {
                return
            }
        }

        val viewModel = CallViewModel(call)
        list.add(viewModel)
        pausedCalls.value = list
    }

    private fun removeCallFromPausedListIfPresent(call: Call) {
        val list = arrayListOf<CallViewModel>()
        list.addAll(pausedCalls.value.orEmpty())

        for (pausedCallViewModel in list) {
            if (pausedCallViewModel.call == call) {
                pausedCallViewModel.destroy()
                list.remove(pausedCallViewModel)
                break
            }
        }

        pausedCalls.value = list
    }
}
