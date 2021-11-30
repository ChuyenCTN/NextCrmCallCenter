package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class NoteResponse(
    @SerializedName("assigned")
    @Expose
    val assigned: Assigned,
    @SerializedName("assigned_to")
    @Expose
    val assignedTo: Int,
    @SerializedName("category")
    @Expose
    val category: Category,
    @SerializedName("category_id")
    @Expose
    val categoryId: Int,
    @SerializedName("causer")
    @Expose
    val causer: Causer,
    @SerializedName("causer_id")
    @Expose
    val causerId: Int,
    @SerializedName("causer_type")
    @Expose
    val causerType: String,
    @SerializedName("code")
    @Expose
    val code: Any,
    @SerializedName("completed_at")
    @Expose
    val completedAt: Any,
    @SerializedName("content")
    @Expose
    val content: String,
    @SerializedName("created_at")
    @Expose
    val createdAt: String,
    @SerializedName("html")
    @Expose
    val html: Any,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("is_complete")
    @Expose
    val isComplete: Boolean,
    @SerializedName("priority")
    @Expose
    val priority: String,
    @SerializedName("status_states")
    @Expose
    val statusStates: String,
    @SerializedName("status_states_info")
    @Expose
    val statusStatesInfo: StatusStatesInfo,
    @SerializedName("subject")
    @Expose
    val subject: String,
    @SerializedName("updated_at")
    @Expose
    val updatedAt: String,
    @SerializedName("user")
    @Expose
    val user: User,
    @SerializedName("user_id")
    @Expose
    val userId: Int
)

data class Assigned(
    @SerializedName("firstname")
    @Expose
    val firstname: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("lastname")
    @Expose
    val lastname: String,
    @SerializedName("name")
    @Expose
    val name: String
)

data class Category(
    @SerializedName("category_type")
    @Expose
    val categoryType: String,
    @SerializedName("description")
    @Expose
    val description: Any,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("parent_id")
    @Expose
    val parentId: Any,
    @SerializedName("picture")
    @Expose
    val picture: Any,
    @SerializedName("short_code")
    @Expose
    val shortCode: Any,
    @SerializedName("slug")
    @Expose
    val slug: Any,
    @SerializedName("tenant_id")
    @Expose
    val tenantId: Int
)

data class Causer(
    @SerializedName("address")
    @Expose
    val address: String,
    @SerializedName("avatar")
    @Expose
    val avatar: Any,
    @SerializedName("birthday")
    @Expose
    val birthday: Any,
    @SerializedName("branch_id")
    @Expose
    val branchId: Any,
    @SerializedName("city")
    @Expose
    val city: Any,
    @SerializedName("code")
    @Expose
    val code: String,
    @SerializedName("contact_description")
    @Expose
    val contactDescription: Any,
    @SerializedName("contact_email")
    @Expose
    val contactEmail: Any,
    @SerializedName("contact_name")
    @Expose
    val contactName: Any,
    @SerializedName("contact_position")
    @Expose
    val contactPosition: Any,
    @SerializedName("contact_tel")
    @Expose
    val contactTel: Any,
    @SerializedName("country")
    @Expose
    val country: Any,
    @SerializedName("created_at")
    @Expose
    val createdAt: String,
    @SerializedName("default")
    @Expose
    val default: Any,
    @SerializedName("deleted_at")
    @Expose
    val deletedAt: Any,
    @SerializedName("description")
    @Expose
    val description: Any,
    @SerializedName("details")
    @Expose
    val details: Any,
    @SerializedName("email")
    @Expose
    val email: String,
    @SerializedName("employee_id")
    @Expose
    val employeeId: Any,
    @SerializedName("facebook_zalo")
    @Expose
    val facebookZalo: Any,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("lat")
    @Expose
    val lat: Any,
    @SerializedName("lng")
    @Expose
    val lng: Any,
    @SerializedName("member_id")
    @Expose
    val memberId: Any,
    @SerializedName("mobile")
    @Expose
    val mobile: String,
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("page_id")
    @Expose
    val pageId: Any,
    @SerializedName("phone")
    @Expose
    val phone: String,
    @SerializedName("psid")
    @Expose
    val psid: Any,
    @SerializedName("sales_stage")
    @Expose
    val salesStage: String,
    @SerializedName("sex")
    @Expose
    val sex: Any,
    @SerializedName("slug")
    @Expose
    val slug: Any,
    @SerializedName("source")
    @Expose
    val source: Any,
    @SerializedName("state")
    @Expose
    val state: Any,
    @SerializedName("status")
    @Expose
    val status: String,
    @SerializedName("street")
    @Expose
    val street: Any,
    @SerializedName("tenant_id")
    @Expose
    val tenantId: Int,
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("updated_at")
    @Expose
    val updatedAt: String,
    @SerializedName("upload_folder")
    @Expose
    val uploadFolder: Any,
    @SerializedName("user_id")
    @Expose
    val userId: Int,
    @SerializedName("user_type")
    @Expose
    val userType: Any,
    @SerializedName("user_zalo_id")
    @Expose
    val userZaloId: Any,
    @SerializedName("website")
    @Expose
    val website: Any,
    @SerializedName("zip")
    @Expose
    val zip: Any
)

data class StatusStatesInfo(
    @SerializedName("code")
    @Expose
    val code: String,
    @SerializedName("color")
    @Expose
    val color: String,
    @SerializedName("description")
    @Expose
    val description: Any,
    @SerializedName("field_id")
    @Expose
    val fieldId: Int,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("order")
    @Expose
    val order: Int,
    @SerializedName("tenant_id")
    @Expose
    val tenantId: Int,
    @SerializedName("value")
    @Expose
    val value: String
)

data class User(
    @SerializedName("firstname")
    @Expose
    val firstname: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("lastname")
    @Expose
    val lastname: String,
    @SerializedName("name")
    @Expose
    val name: String
)