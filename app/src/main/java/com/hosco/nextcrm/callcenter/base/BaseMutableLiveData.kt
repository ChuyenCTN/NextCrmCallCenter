package com.hosco.nextcrm.callcenter.base

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData

class BaseMutableLiveData<T : BaseObservable> : MutableLiveData<T>() {

    var callback: Observable.OnPropertyChangedCallback =
        object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                setValue(value!!)
            }

        }

    override fun setValue(value: T) {
        super.setValue(value)
        value.addOnPropertyChangedCallback(callback)
    }
}