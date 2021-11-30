package com.hosco.nextcrm.callcenter.ui.note


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.api.ApiBuilder
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.FlatmapCrm
import com.hosco.nextcrm.callcenter.model.response.NoteResponse
import com.hosco.nextcrm.callcenter.model.response.PriorityResponse
import com.hosco.nextcrm.callcenter.model.response.StateResponse
import com.hosco.nextcrm.callcenter.model.response.TypeResponse
import com.hosco.nextcrm.callcenter.network.common.ListResponse
import com.hosco.nextcrm.callcenter.network.remote.common.DataResponse
import com.hosco.nextcrm.callcenter.utils.Key
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NoteViewModel : BaseViewModel() {

    private val _dataNotes = MutableLiveData<List<NoteResponse>>()

    private val _curentPage = MutableLiveData<Int>()
    val curentPage: LiveData<Int> = _curentPage

    private val _totalPage = MutableLiveData<Int>()
    val totalPage: LiveData<Int> = _totalPage

    var strSearch = ""
    var categoryId = ""
    var state = ""
    var priority = ""

    var continueGetData = true

    fun showNoteData(): MutableLiveData<List<NoteResponse>> {
        return _dataNotes
    }

    fun setCurentPage(page: Int) {
        _curentPage.value = page
    }

    fun getCurrenPage(): Int? {
        return _curentPage.value
    }

    fun onCreate() {
        strSearch = ""
        categoryId = ""
        state = ""
        priority = ""

        continueGetData = true
        _curentPage.value = 1
        _totalPage.value = curentPage.value?.plus(1)
    }

    fun refreshList() {
        continueGetData = true
        _curentPage.value = 1
        _totalPage.value = curentPage.value?.plus(1)
        getDataContact()
    }

    fun addCurrentPage() {
        if (continueGetData) {
            _curentPage.value = _curentPage.value?.plus(1)
            getDataContact()
        }
    }

    fun setParamSearch(strSearch: String, categotyId: String, state: String, priority: String) {
        this.strSearch = strSearch
        this.categoryId = categotyId
        this.state = state
        this.priority = priority
    }

    fun getDataContact() {
        curentPage.value?.let { curentPage ->
            ApiBuilder.getWebService()
                .getListNotes(Const.PAGE_LIMIT, curentPage, strSearch, categoryId, state, priority)
                ?.flatMap {
                    return@flatMap Observable.just(
                        it
                    )
                }
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe {
                    if (curentPage > 1)
                        showLoadingMore(true)
                    else
                        showLoading(true)
                }
                ?.doFinally {
                    if (curentPage > 1)
                        showLoadingMore(false)
                    else
                        showLoading(false)
                }?.let {
                    disposables.add(
                        it.subscribe({
                            if (it != null) {
                                var data: ListResponse<List<NoteResponse?>?>? = it
                                _totalPage.value = data?.meta?.pagination?.totalPages
                                _dataNotes.value = data?.data as List<NoteResponse>?
                                if (_curentPage.value == _totalPage.value) {
                                    continueGetData = false
                                }
                            } else {
                            }
                        }, {
                            showFailure(it)
                            _curentPage.value = _curentPage.value?.minus(1)
                        })
                    )
                }
        }
    }

    /**
     * filter
     */

    private val _dataState = MutableLiveData<List<StateResponse>?>()
    val dataState: LiveData<List<StateResponse>?> = _dataState

    private val _dataType = MutableLiveData<List<TypeResponse>?>()
    val datatype: LiveData<List<TypeResponse>?> = _dataType

    private val _dataPriority = MutableLiveData<List<PriorityResponse>?>()
    val dataPriority: LiveData<List<PriorityResponse>?> = _dataPriority

    fun getListSate() {
        ApiBuilder.getWebService()
            .getStateList()
            ?.flatMap {
                return@flatMap Observable.just(
                    FlatmapCrm.addItemDefaultStateList(it.data as ArrayList<StateResponse>)
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe {}
            ?.doFinally {}?.let {
                disposables.add(
                    it.subscribe({
                        if (it != null) {
                            var data: List<StateResponse>? = it
//                            showNoData(false)
                            data.let { it2 ->
                                SharePreferenceUtils.getInstances()
                                    .saveStateList(
                                        Key.STATE_LIST_FILTER,
                                        it2 as List<StateResponse>
                                    )
                                _dataState.value = it2
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
                    FlatmapCrm.addItemDefaultTypeList(it.data as ArrayList<TypeResponse>)
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe {}
            ?.doFinally {}?.let {
                disposables.add(
                    it.subscribe({
                        if (it != null) {
                            var data: List<TypeResponse>? = it
                            data.let { it2 ->
                                SharePreferenceUtils.getInstances()
                                    .saveTypeList(Key.TYPE_LIST_FILTER, it2 as List<TypeResponse>)
                                _dataType.value = it2

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
                    FlatmapCrm.addItemDefaultPriority(it.data as ArrayList<PriorityResponse>)
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe {}
            ?.doFinally {}?.let {
                disposables.add(
                    it.subscribe({
                        if (it != null) {
                            var data: List<PriorityResponse>? = it
//                            showNoData(false)
                            data.let { it2 ->
                                SharePreferenceUtils.getInstances()
                                    .savePriorityList(
                                        Key.PRIORITY_LIST_FILTER,
                                        it2 as List<PriorityResponse>
                                    )
                                _dataPriority.value = it2
                            }
                        }
                    }, {
                        showFailure(it)
                    })
                )
            }
    }

    fun getInputData() {
        var stateList = SharePreferenceUtils.getInstances().getStateList(Key.STATE_LIST_FILTER)
        var typeList = SharePreferenceUtils.getInstances().getTypeList(Key.TYPE_LIST_FILTER)
        var priorityList =
            SharePreferenceUtils.getInstances().getPriorityList(Key.PRIORITY_LIST_FILTER)

        if (stateList.isNullOrEmpty()) getListSate() else _dataState.value = stateList
        if (typeList.isNullOrEmpty()) getListType() else _dataType.value = typeList
        if (priorityList.isNullOrEmpty()) getListPriority() else _dataPriority.value = priorityList

    }


}