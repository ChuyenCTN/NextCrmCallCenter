package com.hosco.nextcrm.callcenter.ui.contact

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.api.ApiBuilder
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.model.response.ContactResponse
import com.hosco.nextcrm.callcenter.model.response.InternalResponse
import com.hosco.nextcrm.callcenter.network.common.ListResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import android.provider.ContactsContract

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.hosco.nextcrm.callcenter.model.request.AddNoteRequest
import com.hosco.nextcrm.callcenter.model.request.PhoneInfoRequest
import com.hosco.nextcrm.callcenter.model.response.DeviceContact
import com.hosco.nextcrm.callcenter.model.response.PhoneInfoResponse
import io.reactivex.disposables.CompositeDisposable
import kotlin.collections.ArrayList


class ContactViewModel : BaseViewModel() {

    private val _curentPageCustomer = MutableLiveData<Int>()
    val curentPageCustomer: LiveData<Int> = _curentPageCustomer

    private val _totalPageCustomer = MutableLiveData<Int>()

    var currentPageInternal = 1

    var _totalPageInternal = (currentPageInternal + 1)

    var continueGetCustomer = true

    var continueGetInternal = true

    var strSearchCustomer: String = ""
    var strSearchInternal: String = ""

    private val _dataListCustomer = MutableLiveData<List<ContactResponse>>()
    private val _dataListInternal = MutableLiveData<List<InternalResponse>>()
    private val _dataListDevice = MutableLiveData<List<DeviceContact>>()
    private val _dataPhoneInfo = MutableLiveData<PhoneInfoResponse>()

    private val _isShowLoadingPhoneInfo = MutableLiveData<Boolean>()
    val isShowLoadingPhoneInfo: LiveData<Boolean> = _isShowLoadingPhoneInfo

    fun getCurrenPageCustomer(): Int? {
        return _curentPageCustomer.value
    }

    fun getCurrenPageInternal(): Int {
        return currentPageInternal
    }

    fun loadMoreCustomer() {
        if (continueGetCustomer) {
            _curentPageCustomer.value = _curentPageCustomer.value?.plus(1)
            getCustomerData()
        }
    }

    fun loadMoreInternal() {
        if (continueGetInternal) {
            currentPageInternal = currentPageInternal.plus(1)
            getInternalData()
        }
    }

    fun onCreate() {
        continueGetCustomer = true
        continueGetInternal = true
        strSearchCustomer = ""
        _curentPageCustomer.value = 1
        _totalPageCustomer.value = curentPageCustomer.value?.plus(1)
        currentPageInternal = 1
        _totalPageInternal = currentPageInternal.plus(1)
    }

    fun refreshListCustomer() {
        _curentPageCustomer.value = 1
        _totalPageCustomer.value = curentPageCustomer.value?.plus(1)
        continueGetCustomer = true
        getCustomerData()
    }

    fun refreshListInternal() {
        continueGetInternal = true
        currentPageInternal = 1
        _totalPageInternal = currentPageInternal.plus(1)
        getInternalData()
    }

    @JvmName("setStrSearchCustomer1")
    fun setStrSearchCustomer(value: String) {
        strSearchCustomer = value
    }

    @JvmName("setStrSearchInternal1")
    fun setStrSearchInternal(value: String) {
        strSearchInternal = value
    }

    fun dataCustomerResponse(): MutableLiveData<List<ContactResponse>> {
        return _dataListCustomer
    }

    fun dataInternalResponse(): MutableLiveData<List<InternalResponse>> {
        return _dataListInternal
    }

    fun dataDeviceResponse(): MutableLiveData<List<DeviceContact>> {
        return _dataListDevice
    }

    fun dataPhoneInfoResponse(): MutableLiveData<PhoneInfoResponse> {
        return _dataPhoneInfo
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
                        showLoadingMore(true)
                    else
                        showLoading(true)
                }
                ?.doFinally {
                    if (currentPage > 1)
                        showLoadingMore(false)
                    else
                        showLoading(false)
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
                                showNoData(false)
                            } else if (it == null && _curentPageCustomer.value == 1)
                                showNoData(true)
                        }, {
                            showFailure(it)
                            if (_curentPageCustomer.value == 1)
                                showNoData(true)
                            if (_curentPageCustomer.value!! > 1)
                                _curentPageCustomer.value = _curentPageCustomer.value?.minus(1)
                        })
                    )
                }
        }
    }

    fun getInternalData() {
        currentPageInternal.let { currentPage ->
            ApiBuilder.getWebService()
                .getListInternal(Const.PAGE_LIMIT, currentPage, strSearchInternal)
                ?.flatMap {
                    return@flatMap Observable.just(
                        it
                    )
                }
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnSubscribe {
                    if (currentPage > 1)
                        showLoadingMore(true)
                    else
                        showLoading(true)
                }
                ?.doFinally {
                    if (currentPage > 1)
                        showLoadingMore(false)
                    else
                        showLoading(false)
                }?.let {
                    disposables.add(
                        it.subscribe({
                            if (it != null) {
                                var data: ListResponse<List<InternalResponse?>?>? = it
                                _totalPageInternal = data?.meta?.pagination?.totalPages!!
                                _dataListInternal.value = data?.data as List<InternalResponse>?
                                if (currentPageInternal == _totalPageInternal) {
                                    continueGetInternal = false
                                }
                                showNoData(false)
                            } else if (it == null && currentPageInternal == 1)
                                showNoData(true)
                        }, {
                            showFailure(it)
                            if (currentPageInternal == 1)
                                showNoData(true)
                            if (currentPageInternal > 1)
                                currentPageInternal = currentPageInternal.minus(1)
                        })
                    )
                }
        }
    }

    @SuppressLint("Range")
    fun getDeviceContactList(context: Context) {
        try {
            var listContact = ArrayList<DeviceContact>()
            val cr: ContentResolver = context.getContentResolver()
            val cur: Cursor? = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null
            )
            var curName = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                curName = ContactsContract.Data.DISPLAY_NAME_PRIMARY
            else curName = ContactsContract.Data.DISPLAY_NAME
            if ((if (cur != null) cur.getCount() else 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    val id: String = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)
                    )
                    val name: String = cur?.getString(
                        cur?.getColumnIndex(curName)
                    ) ?: ""
                    if (cur.getInt(
                            cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER
                            )
                        ) > 0
                    ) {
                        val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        while (pCur?.moveToNext() == true) {
                            val phoneNo: String = pCur.getString(
                                pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            listContact.add(
                                DeviceContact(
                                    name = name,
                                    phone = phoneNo.replace("-", "")
                                )
                            )
                        }
                        pCur?.close()
                    }
                }
            }
            _dataListDevice.value = listContact
            if (cur != null) {
                cur.close()
            }
        } catch (e: Exception) {
        }
    }

    fun getPhoneInfo(view: View, phoneInfoRequest: PhoneInfoRequest) {
        ApiBuilder.getWebService().getPhoneInfo(phoneInfoRequest)
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { _isShowLoadingPhoneInfo.value = true }
            ?.doFinally { _isShowLoadingPhoneInfo.value = false }?.let {
                CompositeDisposable().add(
                    it.subscribe({
                        _isShowLoadingPhoneInfo.value = false
                        it.let { dataresponse ->
                            it.meta.let { it ->
                                if (it?.statusCode == 0) {
                                    dataresponse.data.let {
                                        _dataPhoneInfo.value = it
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
                    }, {
                        _isShowLoadingPhoneInfo.value = false
                        showFailure(it)
                    })
                )
            }
    }
}