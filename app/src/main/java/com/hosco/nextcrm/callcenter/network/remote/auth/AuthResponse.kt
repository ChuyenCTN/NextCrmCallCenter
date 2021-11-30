package com.hosco.nextcrm.callcenter.network.remote.auth

import com.google.gson.annotations.SerializedName

open class AuthResponse(
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("token")
    val token: String,
    @SerializedName("user")
    val user: User
)