package com.hosco.nextcrm.callcenter.common

import android.util.Log
import com.hosco.nextcrm.callcenter.model.response.PhoneInfoResponse
import com.hosco.nextcrm.callcenter.model.response.PriorityResponse
import com.hosco.nextcrm.callcenter.model.response.StateResponse
import com.hosco.nextcrm.callcenter.model.response.TypeResponse
import com.hosco.nextcrm.callcenter.network.remote.auth.CustommerResponse
import com.hosco.nextcrm.callcenter.network.remote.common.DataResponse

object FlatmapCrm {
    fun addItemDefaultStateList(
        data: ArrayList<StateResponse>
    ): List<StateResponse> {
        data.let {
            it.add(0, StateResponse("All", "", "", 0, 0, 0, ArrayList<Any>(), ""))
            return it
        }
    }

    fun addItemDefaultTypeList(data: ArrayList<TypeResponse>): List<TypeResponse> {
        data.let {
            it.add(0, TypeResponse("", 0, "All", 0, ""))
            return it
        }
    }

    fun addItemDefaultPriority(data: ArrayList<PriorityResponse>): List<PriorityResponse> {
        data.let {
            it.add(0, PriorityResponse("All", "All"))
            return it
        }
    }

    fun getPhoneInfo(data: DataResponse<Any>): PhoneInfoResponse? {
        data.let {
            it.meta.let {
                if (it?.statusCode == 0&&data.meta!=null) {
                    Log.d("zxcvbnm,.", data.data.toString())
                    var phoneInfoResponse=data.data as PhoneInfoResponse

                } else {
                    return null
                }
            }
        }
        return null
    }
}