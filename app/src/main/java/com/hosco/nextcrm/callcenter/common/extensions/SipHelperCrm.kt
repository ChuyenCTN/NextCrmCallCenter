package com.hosco.nextcrm.callcenter.common.extensions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.network.remote.auth.AuthResponse
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils.Companion.getInstances
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.network.remote.auth.ExtentionConfig
import com.hosco.nextcrm.callcenter.utils.Key.SIP_AVAILABLE
import org.linphone.core.*
import java.util.*

object SipHelperCrm {

    var core: Core = coreContext.core
    var authResponse: AuthResponse? = null
    var account: Account? = null
    var accountToDelete: Account? = null

    private val _stateSip = MutableLiveData<Int>()
    val stateSip: LiveData<Int> = _stateSip

    fun onCreate() {
        core = coreContext.core
        if (authResponse == null)
            authResponse = getInstances().getAuthResponse()
        val defaultAccount = coreContext.core.defaultAccount
        val firstAccount = coreContext.core.accountList.first()

        if (defaultAccount != null)
            account = defaultAccount
        else if (firstAccount != null)
            account = firstAccount

        account?.addListener(listener)

    }

    val listener: AccountListenerStub = object : AccountListenerStub() {
        override fun onRegistrationStateChanged(
            account: Account,
            state: RegistrationState,
            message: String
        ) {
            Log.d(SipHelperCrm::class.java.simpleName, "state   ${state.toString()}")
            Log.d(SipHelperCrm::class.java.simpleName, "message   ${message.toString()}")

            when (state.toString().trim()) {
                "Progress" -> {
                    _stateSip.postValue(Const.SIP_PROGRESS)
                }
                "None" -> {
                    _stateSip.postValue(Const.SIP_DELETE)
                }
                "Failed" -> {
                    _stateSip.postValue(Const.SIP_DELETE)
                }
            }
            if (state == RegistrationState.Cleared && account == accountToDelete) {
                Log.d(
                    SipHelperCrm::class.java.simpleName,
                    " Account to remove registration is now cleared"
                )
                deleteAccount(account)
            } else {
                update()
                if (state == RegistrationState.Ok) {
                    coreContext.contactsManager.updateLocalContacts()
                    _stateSip.postValue(Const.SIP_AVAILABLE)
                }
            }
        }
    }

    fun onLogin() {
        if (getInstances().getToken() != null) {
            if (authResponse == null)
                authResponse = getInstances().getAuthResponse()
            if (getInstances().getBoolean(SIP_AVAILABLE) == true) {
                var accountCreator =
                    coreContext.core.createAccountCreator(corePreferences.xmlRpcServerUrl)
                accountCreator.language = Locale.getDefault().language

                val extenConfig: ExtentionConfig = authResponse?.user?.extentionConfig!!

                accountCreator.username = extenConfig.userName
                accountCreator.password = extenConfig.password
                accountCreator.domain = extenConfig.domain
                accountCreator.displayName = extenConfig.displayName
                accountCreator.transport = TransportType.Udp

                val proxyConfig: ProxyConfig? = accountCreator.createProxyConfig()

                if (proxyConfig == null) {

                    Log.d(
                        SipHelperCrm::class.java.simpleName,
                        "Account creator couldn't create proxy config"
                    )
                    return
                }
                onCreate()
                Log.d(SipHelperCrm::class.java.simpleName, "Proxy config created")
            }
        }
    }

    fun disable(isDisable: Boolean) {
        Log.d(SipHelperCrm::class.java.simpleName, "disable start")
        val params = account?.params?.clone()
        params?.registerEnabled = !isDisable
        if (params != null) {
            account?.params = params
        }
        _stateSip.postValue(Const.SIP_DISABLE)
        Log.d(SipHelperCrm::class.java.simpleName, "disable end")
    }

    fun deleteAccount(account: Account) {
        val authInfo = account?.findAuthInfo()
        if (authInfo != null) {
            Log.d(SipHelperCrm::class.java.simpleName, "deleteaccount authInfo!=null")
            core?.removeAuthInfo(authInfo)
            _stateSip.postValue(Const.SIP_DELETE)
        } else {
            Log.d(SipHelperCrm::class.java.simpleName, "Couldn't find matching auth info...")
        }
    }

    fun delete() {
        accountToDelete = account

        val registered = account?.state == RegistrationState.Ok

        if (core.defaultAccount == account) {
            Log.d(
                SipHelperCrm::class.java.simpleName,
                "Account was default, let's look for a replacement"
            )
            for (accountIterator in core.accountList) {
                if (account != accountIterator) {
                    core.defaultAccount = accountIterator
                    Log.d(
                        SipHelperCrm::class.java.simpleName,
                        "New default account is $accountIterator"
                    )
                    break
                }
            }
        }

        val params = account?.params?.clone()
        params?.registerEnabled = false
        if (params != null) {
            account?.params = params
        }

        if (!registered) {
            Log.d(
                SipHelperCrm::class.java.simpleName,
                " Account isn't registered, don't unregister before removing it"
            )
            account?.let {
                deleteAccount(it)
            }
            account?.removeListener(listener)
        }
    }

    val disable = MutableLiveData<Boolean>()

    private fun update() {
        val params = account?.params
        disable.value = params?.registerEnabled
    }
}
