package com.hosco.nextcrm.callcenter.model.request


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class ContactRequest(
    @SerializedName("address")
    @Expose
    val address: String = "",
    @SerializedName("birthday")
    @Expose
    val birthday: String = "",
    @SerializedName("code")
    @Expose
    val code: String = "",
    @SerializedName("email")
    @Expose
    val email: String = "",
    @SerializedName("mobile")
    @Expose
    val mobile: String = "",
    @SerializedName("name")
    @Expose
    val name: String = "",
    @SerializedName("title")
    @Expose
    val title: String = "",
    @SerializedName("website")
    @Expose
    val website: String = ""
)