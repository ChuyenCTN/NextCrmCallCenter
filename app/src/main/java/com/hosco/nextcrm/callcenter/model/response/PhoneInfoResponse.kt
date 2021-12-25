package com.hosco.nextcrm.callcenter.model.response
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class PhoneInfoResponse(
    @SerializedName("avatar")
    @Expose
    val avatar: Any?,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("phone")
    @Expose
    val phone: String
)