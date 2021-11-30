package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class StateResponse(
    @SerializedName("code")
    @Expose
    val code: String,
    @SerializedName("color")
    @Expose
    val color: String,
    @SerializedName("description")
    @Expose
    val description: Any,
    @SerializedName("field_id")
    @Expose
    val fieldId: Int,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("order")
    @Expose
    val order: Int,
    @SerializedName("users")
    @Expose
    val users: List<Any>,
    @SerializedName("value")
    @Expose
    val value: String
)