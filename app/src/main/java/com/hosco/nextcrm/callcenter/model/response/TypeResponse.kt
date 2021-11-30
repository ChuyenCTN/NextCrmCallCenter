package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class TypeResponse(
    @SerializedName("description")
    @Expose
    val description: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("parent_id")
    @Expose
    val parentId: Int,
    @SerializedName("picture")
    @Expose
    val picture: String
)