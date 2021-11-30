package com.hosco.nextcrm.callcenter.ui.addnote

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.api.ApiBuilder
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.hosco.nextcrm.callcenter.model.request.AddNoteRequest
import com.hosco.nextcrm.callcenter.model.request.CallNoteRequest
import com.hosco.nextcrm.callcenter.model.request.ContactRequest
import com.hosco.nextcrm.callcenter.model.response.NoteResponse
import com.hosco.nextcrm.callcenter.model.response.PriorityResponse
import com.hosco.nextcrm.callcenter.model.response.StateResponse
import com.hosco.nextcrm.callcenter.model.response.TypeResponse
import com.hosco.nextcrm.callcenter.network.remote.common.DataResponse
import com.hosco.nextcrm.callcenter.utils.Key
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddNoteViewModel : BaseViewModel() {
    /**
     * start add note dialog
     */
    fun addNote(phone: String, description: String) {
        val extNumber =
            SharePreferenceUtils.getInstances().getAuthResponse().user.extentionConfig.userName
        val noteRequest = CallNoteRequest("callID", description, extNumber, phone, "")

        ApiBuilder.getWebService().addCallNote(noteRequest)
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

                        }
                    }, { })
                )
            }
    }

    /**
     * end add note dialog
     */

    fun getContactByPhone(phone: String) {
        ApiBuilder.getWebService().getContactByPhone(phone)
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

                        }
                    }, { })
                )
            }
    }


    /**
     * start add note activity
     */

    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading

    private val _dataState = MutableLiveData<List<StateResponse>?>()
    val dataState: LiveData<List<StateResponse>?> = _dataState

    private val _dataType = MutableLiveData<List<TypeResponse>?>()
    val datatype: LiveData<List<TypeResponse>?> = _dataType

    private val _dataPriority = MutableLiveData<List<PriorityResponse>?>()
    val dataPriority: LiveData<List<PriorityResponse>?> = _dataPriority

    private val _dataAddNoteResponse = MutableLiveData<NoteResponse>()
    val dataAddNoteResponse: LiveData<NoteResponse> = _dataAddNoteResponse

    fun getListSate() {
        ApiBuilder.getWebService()
            .getStateList()
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe {}
            ?.doFinally {}?.let {
                disposables.add(
                    it.subscribe({
                        if (it != null) {
                            var data: DataResponse<List<StateResponse?>?>? = it
//                            showNoData(false)
                            data.let { it2 ->
                                it2?.data.let { it3 ->
                                    SharePreferenceUtils.getInstances()
                                        .saveStateList(Key.STATE_LIST, it3 as List<StateResponse>)
                                    _dataState.value = it3
                                }
                            }
                        }
                    }, {
                        showFailure(it)
                    })
                )
            }
    }

    fun getListType() {
        ApiBuilder.getWebService()
            .getTypeList()
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe {}
            ?.doFinally {}?.let {
                disposables.add(
                    it.subscribe({
                        if (it != null) {
                            var data: DataResponse<List<TypeResponse?>?>? = it
                            data.let { it2 ->
                                it2?.data.let { it3 ->
                                    SharePreferenceUtils.getInstances()
                                        .saveTypeList(Key.TYPE_LIST, it3 as List<TypeResponse>)
                                    _dataType.value = it3
                                }
                            }
                        }
                    }, {
                        showFailure(it)
                    })
                )
            }
    }

    fun getListPriority() {
        ApiBuilder.getWebService()
            .getPriorityList()
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe {}
            ?.doFinally {}?.let {
                disposables.add(
                    it.subscribe({
                        if (it != null) {
                            var data: DataResponse<List<PriorityResponse?>?>? = it
//                            showNoData(false)
                            data.let { it2 ->
                                it2?.data.let { it3 ->
                                    SharePreferenceUtils.getInstances()
                                        .savePriorityList(
                                            Key.PRIORITY_LIST,
                                            it3 as List<PriorityResponse>
                                        )
                                    _dataPriority.value = it3
                                }
                            }
                        }
                    }, {
                        showFailure(it)
                    })
                )
            }
    }

    fun getInputData() {
        var stateList = SharePreferenceUtils.getInstances().getStateList(Key.STATE_LIST)
        var typeList = SharePreferenceUtils.getInstances().getTypeList(Key.TYPE_LIST)
        var priorityList = SharePreferenceUtils.getInstances().getPriorityList(Key.PRIORITY_LIST)

        if (stateList.isNullOrEmpty()) getListSate() else _dataState.value = stateList
        if (typeList.isNullOrEmpty()) getListType() else _dataType.value = typeList
        if (priorityList.isNullOrEmpty()) getListPriority() else _dataPriority.value = priorityList

    }

    private fun addNote(view: View, addNoteRequest: AddNoteRequest) {
        ApiBuilder.getWebService().addNote(addNoteRequest)
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { _isShowLoading.value = true }
            ?.doFinally { _isShowLoading.value = false }?.let {
                CompositeDisposable().add(
                    it.subscribe({
                        it.let { dataresponse ->
                            it.meta.let {
                                if (it?.statusCode == 0) {
                                    dataresponse.data.let {
                                        _dataAddNoteResponse.value = it
                                    }
                                } else {
                                    it?.message?.let { it1 ->
                                        DialogUtils.showSnackBarWithListener(
                                            view,
                                            it1, null
                                        )
                                    }
                                }
                            }
                        }
                    }, { showFailure(it) })
                )
            }
    }


    fun setNoteRequest(
        view: View,
        contact: String,
        titleNote: String,
        internalNote: String,
        state: String,
        type: String,
        priority: String
    ) {
        if (contact.isEmpty() || internalNote.isEmpty() || titleNote.isEmpty()) {
            DialogUtils.showSnackBar(view, view.context.getString(R.string.txt_error_valid_common))
            return
        }
        var noteRequest = AddNoteRequest(
            categoryId = type,
            contactId = 0,
            content = internalNote,
            priority = priority,
            statusStates = state,
            subject = titleNote
        )
        noteRequest.apply { addNote(view, this) }

    }


}