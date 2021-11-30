package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ContactByPhoneResponse(
    @SerializedName("email")
    @Expose
    val email: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("mobile")
    @Expose
    val mobile: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("phone")
    @Expose
    val phone: String,
    @SerializedName("user_id")
    @Expose
    val userId: String
)