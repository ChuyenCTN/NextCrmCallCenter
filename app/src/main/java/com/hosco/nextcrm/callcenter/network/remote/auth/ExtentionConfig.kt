package com.hosco.nextcrm.callcenter.network.remote.auth

import com.google.gson.annotations.SerializedName

data class ExtentionConfig(
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("domain")
    val domain: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("transport")
    val transport: String,
    @SerializedName("user_name")
    val userName: String
)