package com.hosco.nextcrm.callcenter.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseViewModel
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.hosco.nextcrm.callcenter.common.extensions.SipHelperCrm
import com.hosco.nextcrm.callcenter.network.remote.auth.*
import com.hosco.nextcrm.callcenter.utils.Key
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.linphone.core.*
import org.linphone.core.tools.Log
import java.util.*
import java.util.concurrent.TimeUnit

class LoginViewModel : BaseViewModel() {
    private val _dataLoginResponse = MutableLiveData<AuthResponse>()
    private val _dataCustomerResponse = MutableLiveData<CustommerResponse>()
    lateinit var authRequest: AuthRequest

    private val _isShowPass = MutableLiveData<Boolean>()
    val isShowPass: LiveData<Boolean> = _isShowPass

    private val _isShowLoading = MutableLiveData<Boolean>()
    val isShowLoading: LiveData<Boolean> = _isShowLoading

    fun dataLoginResponse(): MutableLiveData<AuthResponse> {
        return _dataLoginResponse
    }

    fun dataCustomerResponse(): MutableLiveData<CustommerResponse> {
        return _dataCustomerResponse
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    fun onCreate() {
        _isShowPass.value = false
    }

    private lateinit var core: Core

    fun setIsShowPass() {
        if (_isShowPass.value == true) {
            _isShowPass.value = false
        } else {
            _isShowPass.value = false
        }
    }

    @SuppressLint("CheckResult")
    fun login(authRequest: AuthRequest) {
        AuthBuilder.getAuthService().login(authRequest)
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { _isShowLoading.value = true }
            ?.doFinally { _isShowLoading.value = false }?.let {
                disposables.add(
                    it.subscribe({
                        it.let { dataresponse ->
                            dataresponse.meta.let { meta ->
                                if (meta?.statusCode == 0) {
                                    it.data?.let { it1 -> saveData(authRequest, it1) }
                                    _dataLoginResponse.value = it.data
                                } else {
                                    _errorMessage.value = meta?.message
//                                    meta?.message?.let { it1 -> showError(it1) }
                                }
                            }

                        }
                    }, { showFailure(it) })
                )
            }
    }

    fun checkCustomer(tenantCode: String) {
        AuthBuilder.getAuthService().checkExistCustomer(tenantCode)
            ?.flatMap {
                return@flatMap Observable.just(
                    it
                )
            }
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { _isShowLoading.value = true }
            ?.doFinally { _isShowLoading.value = false }?.let {
                disposables.add(
                    it.subscribe({
                        it.let { dataresponse ->
                            dataresponse.meta.let { meta ->
                                if (meta?.statusCode == 0) {
                                    it.data?.let { it1 ->
                                        _dataCustomerResponse.value = it1
                                        SharePreferenceUtils.getInstances()
                                            .saveCustomerResponse(_dataCustomerResponse.value)
                                    }
                                } else {
                                    _errorMessage.value = meta?.message
                                    meta?.message?.let { it1 -> showError(it1) }
                                }
                            }

                        }
                    }, { showFailure(it) })
                )
            }
    }

    override fun onCleared() {
        disposables.clear()
    }

    fun saveData(authRequest: AuthRequest, authResponse: AuthResponse) {
        SharePreferenceUtils.getInstances().saveToken(authResponse.token)
        SharePreferenceUtils.getInstances().saveAuthRequest(authRequest)
        SharePreferenceUtils.getInstances().saveAuthResponse(authResponse)

        if (authResponse != null && authResponse.user != null && authResponse.user.extentionConfig != null && authResponse.user.extentionConfig.userName != null && authResponse.user.extentionConfig.domain != null) {
            SharePreferenceUtils.getInstances().saveBoolean(Key.SIP_AVAILABLE, true)
        } else {
            SharePreferenceUtils.getInstances().saveBoolean(Key.SIP_AVAILABLE, false)
        }
    }

    fun setAuRequest(view: View, domain: String, username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            DialogUtils.showSnackBar(view, view.context.getString(R.string.txt_error_valid_common))
        } else {
            authRequest = AuthRequest(domain, username, password)
            login(authRequest)
        }
    }

    @SuppressLint("CheckResult")
    fun checkAutoLogin(activity: Activity) {
        if (!SharePreferenceUtils.getInstances().getDomain().isNullOrEmpty()) {
            DialogUtils.showCrmLoadingDialog(
                activity,
                activity.resources.getString(R.string.txt_loading_login)
            )
            if (SharePreferenceUtils.getInstances().getAuthRequest() != null)
                SharePreferenceUtils.getInstances().getAuthRequest()?.let { login(it) }
            else {
                activity.startActivity(Intent(activity, LoginActivity::class.java))
                activity.finish()
            }
        } else {
            Completable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    activity.startActivity(Intent(activity, DomainActivity::class.java))
                    activity.finish()
                }
        }
    }

    fun checkAutoCustomer(activity: Activity) {
        if (SharePreferenceUtils.getInstances().getCustomerResponse() != null)
            SharePreferenceUtils.getInstances()
                .getCustomerResponse()?.code?.let { checkCustomer(it) }

    }

    fun setShowHidePass(editText: EditText, image: ImageView) {
        if (editText.transformationMethod.equals(PasswordTransformationMethod.getInstance())) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            image.setImageDrawable(image.context.getDrawable(R.drawable.ic_baseline_visibility))
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            image.setImageDrawable(image.context.getDrawable(R.drawable.ic_baseline_visibility_off))
        }
    }

    fun loginLinphone(authSipRequest: AuthSipRequest, listener: CoreListenerStub) {
        authSipRequest.let { request ->
            var typeTransport = TransportType.Udp
            if (request.transportType.equals(Const.TYPE_UDP_SIP))
                typeTransport = TransportType.Udp

            val authInfo = Factory.instance().createAuthInfo(
                authSipRequest.userName,
                null,
                authSipRequest.passWord,
                null,
                null,
                authSipRequest.domain,
                null
            )

            // Account object replaces deprecated ProxyConfig object
            // Account object is configured through an AccountParams object that we can obtain from the Core
            val accountParams = core.createAccountParams()

            // A SIP account is identified by an identity address that we can construct from the username and domain
            val identity = Factory.instance()
                .createAddress("sip:${authSipRequest.userName}@${authSipRequest.domain}")
            accountParams.identityAddress = identity

            // We also need to configure where the proxy server is located
            val address = Factory.instance().createAddress("sip:${authSipRequest.domain}")
            // We use the Address object to easily set the transport protocol
            address?.transport = typeTransport
            accountParams.serverAddress = address
            // And we ensure the account will start the registration process
            accountParams.registerEnabled = true

            // Now that our AccountParams is configured, we can create the Account object
            val account = core.createAccount(accountParams)

            // Now let's add our objects to the Core
            core.addAuthInfo(authInfo)
            core.addAccount(account)

            // Also set the newly added account as default
            core.defaultAccount = account

            // Allow account to be removed

            // To be notified of the connection status of our account, we need to add the listener to the Core
            core.addListener(listener)
            // We can also register a callback on the Account object
            account.addListener { _, state, message ->
                // There is a Log helper in org.linphone.core.tools package
                Log.i("[Account] Registration state changed: $state, $message")
            }

            // Finally we need the Core to be started for the registration to happen (it could have been started before)
            core.start()

        }
    }

}

