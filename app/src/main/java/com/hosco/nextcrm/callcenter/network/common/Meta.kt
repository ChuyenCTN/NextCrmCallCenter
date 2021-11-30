package com.hosco.nextcrm.callcenter.network.remote.common

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.hosco.nextcrm.callcenter.network.common.Links

data class Meta (
    @SerializedName("status_code")
    val statusCode: Int? = 0,

    @SerializedName("message")
    val message: String? = null ,

    @SerializedName("pagination")
    val pagination: Pagination? = null

)

data class Pagination(
    @SerializedName("count")
    @Expose
    val count: Int,
    @SerializedName("current_page")
    @Expose
    val currentPage: Int,
    @SerializedName("links")
    @Expose
    val links: Any,
    @SerializedName("per_page")
    @Expose
    val perPage: Int,
    @SerializedName("total")
    @Expose
    val total: Int,
    @SerializedName("total_pages")
    @Expose
    val totalPages: Int
)

data class Links(
    @SerializedName("next")
    @Expose
    val next: String
)