package com.hosco.nextcrm.callcenter.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.api.ApiBuilder
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.model.response.HistoryResponse
import com.hosco.nextcrm.callcenter.network.common.ListResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HistoryViewModel : BaseViewModel() {

    private val _dataHistory = MutableLiveData<List<HistoryResponse>>()

    private var _curentPage: Int = 1

    private var _totalPage: Int = 1


    private val _isShowEdit = MutableLiveData<Boolean>()
    val isShowEdit: LiveData<Boolean> = _isShowEdit

    var continueGetData = true

    fun showHistoryData(): MutableLiveData<List<HistoryResponse>> {
        return _dataHistory
    }

    fun getCurrenPage(): Int? {
        return _curentPage
    }

    fun addCurrentPage() {
        if (continueGetData) {
            _curentPage = _curentPage + 1
            getHistoryData()
        }
    }

    fun onCreate() {
        continueGetData = true
        _curentPage = 1
        _totalPage = _curentPage + 1
        _isShowEdit.value = false
    }

    fun refreshList() {
        _curentPage = 1
        _totalPage = _curentPage + 1
        getHistoryData()
    }

    fun getHistoryData() {
        _curentPage.let { curentPage ->
            ApiBuilder.getWebService().getListHistory(Const.PAGE_LIMIT, curentPage, "")
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
                                var data: ListResponse<List<HistoryResponse?>?>? = it
                                _totalPage = data?.meta?.pagination?.totalPages!!
                                _dataHistory.value = data?.data as List<HistoryResponse>?
                                if (_curentPage == _totalPage) {
                                    continueGetData = false
                                }
                            }
                        }, {
                            showFailure(it)
                            _curentPage = _curentPage.minus(1)
                        })
                    )
                }
        }
    }
}