package com.hosco.nextcrm.callcenter.ui.searchcontact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.api.ApiBuilder
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.model.response.ContactResponse
import com.hosco.nextcrm.callcenter.network.common.ListResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchContactViewModel : BaseViewModel() {

    private val _curentPageCustomer = MutableLiveData<Int>()
    val curentPageCustomer: LiveData<Int> = _curentPageCustomer

    private val _totalPageCustomer = MutableLiveData<Int>()

    var continueGetCustomer = true

    var strSearchCustomer: String = ""

    private val _dataListCustomer = MutableLiveData<List<ContactResponse>>()

    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading

    private val _isShowLoadMore = MutableLiveData<Boolean>()
    val isShowLoadMore: LiveData<Boolean> = _isShowLoadMore

    private val _isNoData = MutableLiveData<Boolean>()
    val isNoData: LiveData<Boolean> = _isNoData

    fun getCurrenPage(): Int? {
        return _curentPageCustomer.value
    }

    fun onCreate() {
        continueGetCustomer = true
        strSearchCustomer = ""
        _curentPageCustomer.value = 1
        _totalPageCustomer.value = curentPageCustomer.value?.plus(1)
    }

    fun refreshListCustomer() {
        _curentPageCustomer.value = 1
        _totalPageCustomer.value = curentPageCustomer.value?.plus(1)
        continueGetCustomer = true
        getCustomerData()
    }

    @JvmName("setStrSearchCustomer1")
    fun setStrSearchCustomer(value: String) {
        strSearchCustomer = value
    }

    fun loadMoreCustomer() {
        if (continueGetCustomer) {
            _curentPageCustomer.value = _curentPageCustomer.value?.plus(1)
            getCustomerData()
        }
    }

    fun dataCustomerResponse(): MutableLiveData<List<ContactResponse>> {
        return _dataListCustomer
    }

    fun getCustomerData() {
        curentPageCustomer.value?.let { currentPage ->
            ApiBuilder.getWebService()
                .getListContacts(Const.PAGE_LIMIT, currentPage, strSearchCustomer)
                ?.flatMap {
                    return@flatMap Observable.just(
                        it
                    )
                }
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe {
                    if (currentPage > 1)
                        _isShowLoadMore.value = true
                    else
                        _isShowLoading.value = true
                }
                ?.doFinally {
                    if (currentPage > 1)
                        _isShowLoadMore.value = false
                    else
                        _isShowLoading.value = false
                }?.let {
                    disposables.add(
                        it.subscribe({
                            if (it != null) {
                                var data: ListResponse<List<ContactResponse?>?>? = it
                                _totalPageCustomer.value = data?.meta?.pagination?.totalPages
                                _dataListCustomer.value = data?.data as List<ContactResponse>?
                                if (_curentPageCustomer.value == _totalPageCustomer.value) {
                                    continueGetCustomer = false
                                }
                                _isNoData.value = false
                            } else if (it == null && _curentPageCustomer.value == 1)
                                _isNoData.value = true
                        }, {
                            showFailure(it)
                            if (_curentPageCustomer.value == 1)
                                _isNoData.value = true
                            if (_curentPageCustomer.value!! > 1)
                                _curentPageCustomer.value = _curentPageCustomer.value?.minus(1)
                        })
                    )
                }
        }
    }
}