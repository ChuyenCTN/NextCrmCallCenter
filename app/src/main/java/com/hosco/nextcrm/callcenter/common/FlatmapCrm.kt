package com.hosco.nextcrm.callcenter.common

import android.graphics.Movie
import com.hosco.nextcrm.callcenter.api.ConfigAPI
import com.hosco.nextcrm.callcenter.model.response.PriorityResponse
import com.hosco.nextcrm.callcenter.model.response.StateResponse
import com.hosco.nextcrm.callcenter.model.response.TypeResponse

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
}