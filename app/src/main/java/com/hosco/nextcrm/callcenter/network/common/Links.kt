package com.hosco.nextcrm.callcenter.network.common


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Links(
    @SerializedName("next")
    @Expose
    val next: String
)