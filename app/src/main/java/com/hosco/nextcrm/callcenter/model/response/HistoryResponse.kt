package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class HistoryResponse(
    @SerializedName("call_id")
    @Expose
    val callId: String,
    @SerializedName("description")
    @Expose
    val description: Any,
    @SerializedName("direction_text")
    @Expose
    val directionText: String,
    @SerializedName("duration")
    @Expose
    val duration: Int,
    @SerializedName("employee_id")
    @Expose
    val employeeId: Any,
    @SerializedName("from_number")
    @Expose
    val fromNumber: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("recording_url")
    @Expose
    val recordingUrl: String,
    @SerializedName("status")
    @Expose
    val status: String,
    @SerializedName("subject")
    @Expose
    val subject: Subject,
    @SerializedName("subject_name")
    @Expose
    val subjectName: String,
    @SerializedName("subject_type")
    @Expose
    val subjectType: String,
    @SerializedName("time_started")
    @Expose
    val timeStarted: String,
    @SerializedName("to_number")
    @Expose
    val toNumber: String,
    @SerializedName("user")
    @Expose
    val user: User,

    val isRemove: Boolean
)