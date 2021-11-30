package com.hosco.nextcrm.callcenter.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hosco.nextcrm.callcenter.CallCenterApplication
import com.hosco.nextcrm.callcenter.model.response.PriorityResponse
import com.hosco.nextcrm.callcenter.model.response.StateResponse
import com.hosco.nextcrm.callcenter.model.response.TypeResponse
import com.hosco.nextcrm.callcenter.network.remote.auth.AuthRequest
import com.hosco.nextcrm.callcenter.network.remote.auth.AuthResponse
import com.hosco.nextcrm.callcenter.network.remote.auth.CustommerResponse
import java.lang.reflect.Type

object Key {
    const val USER_INFO_RESPONSE = "data_user_info_response"
    const val USER_INFO_REQUEST = "data_user_info_request"
    const val CUSTOMER_INFO_RESPONSE = "data_customer_response"
    const val TOKEN = "data_token"
    const val DOMAIN = "tenant_code"
    const val STATE_LIST = "state_list"
    const val TYPE_LIST = "type_list"
    const val PRIORITY_LIST = "priority_list"
    const val STATE_LIST_FILTER = "state_list_filter"
    const val TYPE_LIST_FILTER = "type_list_filter"
    const val PRIORITY_LIST_FILTER = "priority_list_filter"
}

class SharePreferenceUtils {
    val name = "NextCrmCallCenter"

    private constructor() {
        mPrefs = CallCenterApplication.context?.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    companion object {
        private var mPrefs: SharedPreferences? = null
        private var instance: SharePreferenceUtils = SharePreferenceUtils()
        fun getInstances(): SharePreferenceUtils {
            if (instance == null) {
                instance = SharePreferenceUtils()
            }
            return instance
        }
    }

    fun saveString(key: String, value: String?) {
        mPrefs?.edit()?.putString(key, value)?.apply()
    }

    fun removeString(key: String) {
        mPrefs?.edit()?.remove(key)?.apply()
    }

    fun getString(key: String): String? {
        return mPrefs?.getString(key, "")
    }

    fun saveInt(key: String, content: Int?) {
        content?.let {
            mPrefs?.edit()?.putInt(key, content)?.apply()
        }
    }

    fun getInt(key: String): Int {
        return mPrefs?.getInt(key, -1) ?: -1
    }

    fun saveToken(token: String?) {
        mPrefs?.edit()?.putString(Key.TOKEN, token)?.apply()
    }

    fun getToken(): String {
        return mPrefs?.getString(Key.TOKEN, "").toString()
    }

    fun saveDomain(domain: String?) {
        mPrefs?.edit()?.putString(Key.DOMAIN, domain)?.apply()
    }

    fun getDomain(): String {
        return mPrefs?.getString(Key.DOMAIN, "").toString()
    }

    fun saveAuthRequest(authRequest: AuthRequest?) {
        authRequest.let {
            mPrefs?.edit()?.putString(Key.USER_INFO_REQUEST, Gson().toJson(it))?.apply()
        }
    }

    fun getAuthRequest(): AuthRequest? {
        val json = mPrefs?.getString(Key.USER_INFO_REQUEST, null) ?: return null
        return Gson().fromJson(json, AuthRequest::class.java)
    }

    fun saveAuthResponse(authResponse: AuthResponse?) {
        authResponse.let {
            mPrefs?.edit()?.putString(Key.USER_INFO_RESPONSE, Gson().toJson(it))?.apply()
        }
    }

    fun getAuthResponse(): AuthResponse {
        val json = mPrefs?.getString(Key.USER_INFO_RESPONSE, null)
        return Gson().fromJson(json, AuthResponse::class.java)
    }

    fun saveCustomerResponse(tenantcode: CustommerResponse?) {
        tenantcode.let {
            mPrefs?.edit()?.putString(Key.CUSTOMER_INFO_RESPONSE, Gson().toJson(it))?.apply()
        }
    }

    fun getCustomerResponse(): CustommerResponse? {
        val json = mPrefs?.getString(Key.CUSTOMER_INFO_RESPONSE, null)
        return Gson().fromJson(json, CustommerResponse::class.java)
    }

    fun saveStateList(keyType: String, data: List<StateResponse>) {
        data.let {
            mPrefs?.edit()?.putString(keyType, Gson().toJson(it))?.apply()
        }
    }

    fun getStateList(keyType: String): List<StateResponse>? {
        var json = mPrefs?.getString(keyType, null)
        val type: Type = object : TypeToken<List<StateResponse?>?>() {}.type
        return Gson().fromJson(json, type)
    }

    fun saveTypeList(keyType: String, data: List<TypeResponse>) {
        data.let {
            mPrefs?.edit()?.putString(keyType, Gson().toJson(it))?.apply()
        }
    }

    fun getTypeList(keyType: String): List<TypeResponse>? {
        var json = mPrefs?.getString(keyType, null)
        val type: Type = object : TypeToken<List<TypeResponse?>?>() {}.type
        return Gson().fromJson(json, type)
    }

    fun savePriorityList(keyType: String, data: List<PriorityResponse>) {
        data.let {
            mPrefs?.edit()?.putString(keyType, Gson().toJson(it))?.apply()
        }
    }

    fun getPriorityList(keyType: String): List<PriorityResponse>? {
        var json = mPrefs?.getString(keyType, null)
        val type: Type = object : TypeToken<List<PriorityResponse?>?>() {}.type
        return Gson().fromJson(json, type)
    }

    fun logout() {
        val domain = getDomain()
        mPrefs?.edit()?.clear()?.commit()
        saveDomain(domain)
    }
}