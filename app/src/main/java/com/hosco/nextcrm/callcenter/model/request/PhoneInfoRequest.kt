package com.hosco.nextcrm.callcenter.model.request

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class PhoneInfoRequest(
    @SerializedName("contact_id")
    @Expose
    val contactId: String,
    @SerializedName("mobile")
    @Expose
    val mobile: String
)