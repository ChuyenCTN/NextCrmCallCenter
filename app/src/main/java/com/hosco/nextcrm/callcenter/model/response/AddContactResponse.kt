package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class AddContactResponse(
    @SerializedName("address")
    @Expose
    val address: String,
    @SerializedName("birthday")
    @Expose
    val birthday: String,
    @SerializedName("code")
    @Expose
    val code: String,
    @SerializedName("created_at")
    @Expose
    val createdAt: String,
    @SerializedName("email")
    @Expose
    val email: String,
    @SerializedName("employee_id")
    @Expose
    val employeeId: Int,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("mobile")
    @Expose
    val mobile: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("tenant_id")
    @Expose
    val tenantId: Int,
    @SerializedName("updated_at")
    @Expose
    val updatedAt: String,
    @SerializedName("website")
    @Expose
    val website: String
)