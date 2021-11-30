package com.hosco.nextcrm.callcenter.model.response


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Subject(
    @SerializedName("address")
    @Expose
    val address: Any,
    @SerializedName("avatar")
    @Expose
    val avatar: String,
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
    val description: String,
    @SerializedName("details")
    @Expose
    val details: Any,
    @SerializedName("email")
    @Expose
    val email: Any,
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
    val pageId: String,
    @SerializedName("phone")
    @Expose
    val phone: Any,
    @SerializedName("psid")
    @Expose
    val psid: String,
    @SerializedName("sales_stage")
    @Expose
    val salesStage: Any,
    @SerializedName("sex")
    @Expose
    val sex: Any,
    @SerializedName("slug")
    @Expose
    val slug: Any,
    @SerializedName("source")
    @Expose
    val source: String,
    @SerializedName("state")
    @Expose
    val state: Any,
    @SerializedName("status")
    @Expose
    val status: Any,
    @SerializedName("street")
    @Expose
    val street: Any,
    @SerializedName("tenant_id")
    @Expose
    val tenantId: Int,
    @SerializedName("title")
    @Expose
    val title: Any,
    @SerializedName("updated_at")
    @Expose
    val updatedAt: String,
    @SerializedName("upload_folder")
    @Expose
    val uploadFolder: Any,
    @SerializedName("user_id")
    @Expose
    val userId: Any,
    @SerializedName("user_type")
    @Expose
    val userType: Any,
    @SerializedName("user_zalo_id")
    @Expose
    val userZaloId: Any,
    @SerializedName("utm_campaign")
    @Expose
    val utmCampaign: Any,
    @SerializedName("utm_content")
    @Expose
    val utmContent: Any,
    @SerializedName("utm_id")
    @Expose
    val utmId: Any,
    @SerializedName("utm_medium")
    @Expose
    val utmMedium: Any,
    @SerializedName("utm_ref_code")
    @Expose
    val utmRefCode: Any,
    @SerializedName("utm_seller_code")
    @Expose
    val utmSellerCode: Any,
    @SerializedName("utm_source")
    @Expose
    val utmSource: Any,
    @SerializedName("utm_term")
    @Expose
    val utmTerm: Any,
    @SerializedName("website")
    @Expose
    val website: Any,
    @SerializedName("zip")
    @Expose
    val zip: Any
)