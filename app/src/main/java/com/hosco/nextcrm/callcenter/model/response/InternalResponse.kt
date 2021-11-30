package com.hosco.nextcrm.callcenter.model.response
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class InternalResponse(
    @SerializedName("crm_extension_id")
    @Expose
    val crmExtensionId: String,
    @SerializedName("designation")
    @Expose
    val designation: String,
    @SerializedName("email")
    @Expose
    val email: String,
    @SerializedName("firstname")
    @Expose
    val firstname: String?,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("lastname")
    @Expose
    val lastname: String,
    @SerializedName("picture")
    @Expose
    val picture: String,
    @SerializedName("tel")
    @Expose
    val tel: Any,
    @SerializedName("username")
    @Expose
    val username: String
)