package com.hosco.nextcrm.callcenter.model.request


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class CallNoteRequest(
    @SerializedName("call_id")
    @Expose
    val callId: String,
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("ext_number")
    @Expose
    val extNumber: String,
    @SerializedName("phone")
    @Expose
    val phone: String,
    @SerializedName("time")
    @Expose
    val time: String
)