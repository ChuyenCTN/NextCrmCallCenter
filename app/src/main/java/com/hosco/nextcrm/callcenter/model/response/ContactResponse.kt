package com.hosco.nextcrm.callcenter.model.response

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ContactResponse(
    @SerializedName("address")
    @Expose
    val address: String,
    @SerializedName("birthday")
    @Expose
    val birthday: Any,
    @SerializedName("code")
    @Expose
    val code: String,
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
    val phone: Any,
    @SerializedName("sex")
    @Expose
    val sex: String,
    @SerializedName("website")
    @Expose
    val website: String
)