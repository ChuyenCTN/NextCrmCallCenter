package com.hosco.nextcrm.callcenter.network.common


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hosco.nextcrm.callcenter.network.remote.common.DataResponse

class ListResponse<T> : DataResponse<T>() {
    @SerializedName("recordsFiltered")
    @Expose
    val recordsFiltered: Int? = null

    @SerializedName("recordsTotal")
    @Expose
    val recordsTotal: Int? = null

    @SerializedName("request")
    @Expose
    val request: Request? = null
}

data class Request(
    @SerializedName("page")
    @Expose
    val page: String,
    @SerializedName("pageLimit")
    @Expose
    val pageLimit: String,
    @SerializedName("password")
    @Expose
    val password: String,
    @SerializedName("strSearch")
    @Expose
    val strSearch: String,
    @SerializedName("tenant_code")
    @Expose
    val tenantCode: String,
    @SerializedName("username")
    @Expose
    val username: String
)