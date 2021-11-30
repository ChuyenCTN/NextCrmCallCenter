package com.hosco.nextcrm.callcenter.network.remote.common

import com.google.gson.annotations.SerializedName

open class BaseResponse(
    @SerializedName("meta")
    var meta: Meta? = null
)