package com.hosco.nextcrm.callcenter.ui.addcontact

import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.api.ApiBuilder
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.hosco.nextcrm.callcenter.model.request.ContactRequest
import com.hosco.nextcrm.callcenter.model.response.AddContactResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddContactViewModel : BaseViewModel() {

    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading

    private val _dataAddContact = MutableLiveData<AddContactResponse>()
    val dataAddContact: LiveData<AddContactResponse> = _dataAddContact

    private fun addContact(view: View, contactRequest: ContactRequest) {
        ApiBuilder.getWebService().addContact(contactRequest)
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
                                    Toast.makeText(view.context, it.message, Toast.LENGTH_SHORT)
                                        .show()
                                    _dataAddContact.value = dataresponse.data
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

    fun setContactRequest(
        view: View,
        company: String,
        name: String,
        extension: String,
        numberPhone: String,
        email: String
    ) {
        if (name.isEmpty() || numberPhone.isEmpty()) {
            DialogUtils.showSnackBar(view, view.context.getString(R.string.txt_error_valid_common))
            return
        }
        if (email.isNotEmpty() && !email.isValidEmail(email)) {
            DialogUtils.showSnackBar(view, view.context.getString(R.string.txt_error_valid_email))
            return
        }
        var contactRequest = ContactRequest(
            address = company,
            birthday = "",
            code = "",
            email = email,
            mobile = numberPhone,
            name = name,
            title = "",
            website = ""
        )
        contactRequest.apply { addContact(view, this) }

    }

    fun CharSequence?.isValidEmail(email: String) =
        !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()


}