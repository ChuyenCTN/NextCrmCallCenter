package com.hosco.nextcrm.callcenter.ui.setting

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.network.remote.auth.AuthResponse
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hosco.nextcrm.callcenter.utils.GenColorBackground


class SettingViewModel : BaseViewModel() {

    private val _authResponse = MutableLiveData<AuthResponse>()
    var authResponse: AuthResponse = SharePreferenceUtils.getInstances().getAuthResponse()

    private val _fullName = MutableLiveData<String>()
    val fullName: LiveData<String> = _fullName

    private val _tel = MutableLiveData<String>()
    val tel: LiveData<String> = _tel

    private val _domain = MutableLiveData<String>()
    val domain: LiveData<String> = _domain

    private val _extension = MutableLiveData<String>()
    val extension: LiveData<String> = _extension

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _phoneNumbers = MutableLiveData<String>()
    val phoneNumbers: LiveData<String> = _phoneNumbers

    private val _version = MutableLiveData<String>()
    val version: LiveData<String> = _version

    fun onCreate() {
//        if (this::authResponse.isLateinit) {
//            authResponse = SharePreferenceUtils.getInstances().getAuthResponse()
//        }
    }

    fun fillData() {
        _fullName.value = getFullName1()
        _tel.value = getTelUser()
        _domain.value = getDomainUser()
        _extension.value = getExtensionUser()
        _email.value = getEmailUser()
    }

    fun getFullName1(): String {
        var fullname = ""
        authResponse.let { authResponse ->
            authResponse.user.let {
                it.firstname.let {
                    fullname = it
                }
                it.lastname.let {
                    fullname = fullname.plus(it).replace("  ", "")
                }
            }
        }
        return fullname
    }

    fun getTelUser(): String {
        authResponse.let {
            it.user.let {
                it.tel.let {
                    return it as String
                }
            }
        }
    }

    fun getEmailUser(): String {
        authResponse.let {
            it.user.let {
                it.email.let {
                    return it as String
                }
            }
        }
    }

    fun getDomainUser(): String {
        authResponse.let {
            it.user.let {
                it.extentionConfig.let {
                    it.domain.let { return it }
                }
            }
        }
    }

    fun getExtensionUser(): String {
        authResponse.let {
            it.user.let {
                it.extentionConfig.let {
                    it.displayName.let { return it }
                }
            }
        }
    }

    fun getVersionApp(context: Context) {
        try {
            val pInfo: PackageInfo =
                context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
            val version = pInfo.versionName
            _version.value = version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}

@BindingAdapter("textBorder")
fun loadTextBoder(view: TextView, text: String?) {
    text?.let {
        if (text.isNotEmpty())
            if (text.length >= 2) {
                if (text.contains(" "))
                    view.text = text.substring(0, 1).plus(text.split(" ")[1].substring(0, 1))
                else
                    view.text =
                        text.substring(0, 1).plus(text.substring(text.length - 2, text.length - 1))
            } else {
                view.text = text.substring(0, 1)
            }
        view.background = GenColorBackground.getBackgroundWithBorder()
    }
}