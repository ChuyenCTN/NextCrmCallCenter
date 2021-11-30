package com.hosco.nextcrm.callcenter.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel : ViewModel() {
    val eventLoading = MutableLiveData<Event<Boolean>>()
    val eventLoadingMore = MutableLiveData<Event<Boolean>>()
    val eventError = MutableLiveData<Event<BaseResponse>>()
    val eventErrorMessage = MutableLiveData<Event<String>>()
    val eventFailure = MutableLiveData<Event<Throwable>>()
    val eventNoData = MutableLiveData<Event<Boolean>>()
    val disposables = CompositeDisposable()

    fun showLoading(value: Boolean) {
        eventLoading.value = Event(value)
    }

    fun showLoadingMore(value: Boolean) {
        eventLoadingMore.value = Event(value)
    }

    fun showError(baseResponse: BaseResponse) {
        eventError.value = Event(baseResponse)
    }

    fun showError(message: String) {
        eventErrorMessage.value = Event(message)
    }

    fun showFailure(throwable: Throwable) {
        eventFailure.value = Event(throwable)
    }

    fun showNoData(value: Boolean) {
        eventNoData.value = Event(value)
    }
}