package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class PriorityResponse(
    @SerializedName("id")
    @Expose
    val id: String,
    @SerializedName("name")
    @Expose
    val name: String
)