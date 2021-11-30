package com.hosco.nextcrm.callcenter.model.request


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class AddNoteRequest(
    @SerializedName("category_id")
    @Expose
    val categoryId: String,
    @SerializedName("contact_id")
    @Expose
    val contactId: Int,
    @SerializedName("content")
    @Expose
    val content: String,
    @SerializedName("priority")
    @Expose
    val priority: String,
    @SerializedName("status_states")
    @Expose
    val statusStates: String,
    @SerializedName("subject")
    @Expose
    val subject: String
)