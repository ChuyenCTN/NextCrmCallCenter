package org.linphone.activities.call.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.*
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.api.ApiBuilder
import com.hosco.nextcrm.callcenter.common.extensions.AllContactList
import com.hosco.nextcrm.callcenter.model.response.DeviceContact
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.linphone.compatibility.Compatibility
import org.linphone.contact.GenericContactViewModel
import org.linphone.core.Call
import org.linphone.core.CallListenerStub
import org.linphone.core.Factory
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.FileUtils
import java.util.*

class CallViewModelFactory(private val call: Call) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CallViewModel(call) as T
    }
}

@SuppressLint("NullSafeMutableLiveData")
open class CallViewModel(val call: Call) : GenericContactViewModel(call.remoteAddress) {
    val address: String by lazy {
        SharePreferenceUtils.getInstances().getAuthResponse().user.extentionConfig.displayName
    }

    val isPaused = MutableLiveData<Boolean>()

    val isOutgoingEarlyMedia = MutableLiveData<Boolean>()

    val callEndedEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    val callConnectedEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    private val _callState = MutableLiveData<String>()
    val callState: LiveData<String> = _callState

    private val _nameContact = MutableLiveData<String>()
    val nameContact: LiveData<String> = _nameContact

    private val _phoneContact = MutableLiveData<String>()
    val phoneContact: LiveData<String> = _phoneContact

    private val _isShowNumberPhone = MutableLiveData<Boolean>()
    val isShowNumberPhone: LiveData<Boolean> = _isShowNumberPhone

    private var timer: Timer? = null

    private val listener = object : CallListenerStub() {
        override fun onStateChanged(call: Call, state: Call.State, message: String) {
            if (call != this@CallViewModel.call) return

            isPaused.value = state == Call.State.Paused
            isOutgoingEarlyMedia.value = state == Call.State.OutgoingEarlyMedia
            _callState.value = coreContext.context.getString(R.string.is_calling)
            if (state == Call.State.End || state == Call.State.Released || state == Call.State.Error) {
                timer?.cancel()
                callEndedEvent.value = Event(true)

                _callState.value = coreContext.context.getString(R.string.call_ending)

                if (state == Call.State.Error) {
                    Log.e("[Call View Model] Error state reason is ${call.reason}")
                }
            } else if (call.state == Call.State.Connected) {
                callConnectedEvent.value = Event(true)
            } else if (call.state == Call.State.StreamsRunning) {
                // Stop call update timer once user has accepted or declined call update
                timer?.cancel()
            } else if (call.state == Call.State.UpdatedByRemote) {
                // User has 30 secs to accept or decline call update
                // Dialog to accept or decline is handled by CallsViewModel & ControlsFragment
                startTimer(call)
            }
        }

        override fun onSnapshotTaken(call: Call, filePath: String) {
            Log.i("[Call View Model] Snapshot taken, saved at $filePath")
            val content = Factory.instance().createContent()
            content.filePath = filePath
            content.type = "image"
            content.subtype = "jpeg"
            content.name = filePath.substring(filePath.indexOf("/") + 1)

            viewModelScope.launch {
                if (Compatibility.addImageToMediaStore(coreContext.context, content)) {
                    Log.i("[Call View Model] Adding snapshot ${content.name} to Media Store terminated")
                } else {
                    Log.e("[Call View Model] Something went wrong while copying file to Media Store...")
                }
            }
        }
    }

    init {
        _callState.value = coreContext.context.getString(R.string.is_calling)
        call.addListener(listener)

        isPaused.value = call.state == Call.State.Paused

        val deviceContact =
            AllContactList.getInstance().getContact(AllContactList.getInstance().temporaryPhone)
        if (deviceContact != null) {
            _nameContact.value = deviceContact.name
            _phoneContact.value = " - ${deviceContact.phone}"
            _isShowNumberPhone.value = true
        } else {
            getContactByPhone(AllContactList.getInstance().temporaryPhone)
            _nameContact.value = AllContactList.getInstance().temporaryPhone
            _isShowNumberPhone.value = false
        }
        Log.i("[Call View Model] init")

    }

    override fun onCleared() {
        destroy()
        super.onCleared()
    }

    fun destroy() {
        call.removeListener(listener)
    }

    fun terminateCall() {
        coreContext.terminateCall(call)
    }

    fun pause() {
        call.pause()
    }

    fun resume() {
        call.resume()
    }

    fun takeScreenshot() {
        if (call.currentParams.videoEnabled()) {
            val fileName = System.currentTimeMillis().toString() + ".jpeg"
            call.takeVideoSnapshot(FileUtils.getFileStoragePath(fileName).absolutePath)
        }
    }

    private fun startTimer(call: Call) {
        timer?.cancel()

        timer = Timer("Call update timeout")
        timer?.schedule(
            object : TimerTask() {
                override fun run() {
                    // Decline call update
                    viewModelScope.launch {
                        withContext(Dispatchers.Main) {
                            coreContext.answerCallVideoUpdateRequest(call, false)
                        }
                    }
                }
            },
            30000
        )
    }

    fun getContactByPhone(numberPhone: String) {
        var name = ""
        var phone = ""
        ApiBuilder.getWebService().getContactByPhone(numberPhone)
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { }
            ?.doFinally { }?.let {
                CompositeDisposable().add(
                    it.subscribe({
                        it.let { dataresponse ->
                            dataresponse.data.let { contactResponse ->
                                name = contactResponse?.name.toString()
                                phone = contactResponse?.phone.toString()
                                _nameContact.value = name
                                _phoneContact.value = phone
                                val deviceContact = DeviceContact(name, phone)
                                EventBus.getDefault().post(deviceContact)
                                AllContactList.getInstance()
                                    .addDeviceToListContact(deviceContact)
                            }
                        }
                    }, { })
                )
            }
    }
}
