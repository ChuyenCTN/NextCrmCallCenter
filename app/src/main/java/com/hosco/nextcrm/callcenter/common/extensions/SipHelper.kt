package com.hosco.nextcrm.callcenter.common.extensions

import android.content.Context
import com.hosco.nextcrm.callcenter.network.remote.auth.AuthResponse
import com.hosco.nextcrm.callcenter.network.remote.auth.ExtentionConfig
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import org.linphone.core.*
import org.linphone.core.tools.Log

object SipHelper {

    var core: Core? = null
    var authResponse: AuthResponse? = null

    fun onCreate(context: Context) {
        core = Factory.instance().createCore(null, null, context)
        authResponse = SharePreferenceUtils.getInstances().getAuthResponse()
    }

    fun login(coreListenerStub: CoreListenerStub?) {
        if (SharePreferenceUtils.getInstances().getToken() != null) {
            val extenConfig: ExtentionConfig = authResponse?.user?.extentionConfig!!

            val username = extenConfig.userName
            val password = extenConfig.password
            val domain = extenConfig.domain

            val transportType = TransportType.Udp


            val authInfo = Factory.instance()
                .createAuthInfo(username, null, password, null, null, domain, null)

            val accountParams = core?.createAccountParams()

            val identity = Factory.instance().createAddress("sip:$username@$domain")
            accountParams?.identityAddress = identity

            val address = Factory.instance().createAddress("sip:$domain")
            address?.transport = transportType
            accountParams?.serverAddress = address
            accountParams?.registerEnabled = true

            val account = accountParams?.let { core?.createAccount(it) }


            core?.addAuthInfo(authInfo)
            if (account != null) {
                core?.addAccount(account)
            }

            core?.defaultAccount = account

            // ready delete account
            // findViewById<Button>(R.id.delete).isEnabled = true
            coreListenerStub.let {
                core?.addListener(it)
            }
            account?.addListener { _, state, message ->
                Log.i("[Account] Registration state changed: $state, $message")
            }

            core?.start()
        }
    }

    fun unregister() {
        // Here we will disable the registration of our Account
        val account = core?.defaultAccount
        account ?: return

        val params = account.params
        // Returned params object is const, so to make changes we first need to clone it
        val clonedParams = params.clone()

        // Now let's make our changes
        clonedParams.registerEnabled = false

        // And apply them
        account.params = clonedParams
    }

    fun delete() {
        // To completely remove an Account
        val account = core?.defaultAccount
        account ?: return
        core?.removeAccount(account)

        // To remove all accounts use
        core?.clearAccounts()

        // Same for auth info
        core?.clearAllAuthInfo()
    }

    private val coreListener = object : CoreListenerStub() {
        override fun onAccountRegistrationStateChanged(
            core: Core,
            account: Account,
            state: RegistrationState,
            message: String
        ) {


            // If account has been configured correctly, we will go through Progress and Ok states
            // Otherwise, we will be Failed.
//            findViewById<TextView>(R.id.registration_status).text = message
//
            if (state == RegistrationState.Failed || state == RegistrationState.Cleared) {
//                findViewById<Button>(R.id.connect).isEnabled = true
            } else if (state == RegistrationState.Ok) {
//                findViewById<Button>(R.id.disconnect).isEnabled = true
            }
        }
    }


}