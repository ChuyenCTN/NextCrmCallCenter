package com.hosco.nextcrm.callcenter.network.remote.auth


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class CustommerResponse(
    @SerializedName("code")
    @Expose
    val code: String,
    @SerializedName("name")
    @Expose
    val name: String
)