package com.hosco.nextcrm.callcenter.network.remote.auth
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email")
    val email: String,
    @SerializedName("employee_id")
    val employeeId: Int,
    @SerializedName("extention_config")
    val extentionConfig: ExtentionConfig,
    @SerializedName("firstname")
    val firstname: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("lastname")
    val lastname: String,
    @SerializedName("picture")
    val picture: String,
    @SerializedName("tel")
    val tel: Any,
    @SerializedName("username")
    val username: String
)