package com.hosco.nextcrm.callcenter.network.remote

import com.hosco.nextcrm.callcenter.model.request.AddNoteRequest
import com.hosco.nextcrm.callcenter.model.request.ContactRequest
import com.hosco.nextcrm.callcenter.model.request.CallNoteRequest
import com.hosco.nextcrm.callcenter.model.response.*
import com.hosco.nextcrm.callcenter.network.common.ListResponse
import com.hosco.nextcrm.callcenter.network.remote.common.DataResponse
import io.reactivex.Observable
import retrofit2.http.*


interface ApiInterface {

    @GET("api/app-call-center/contact")
    fun getListContacts(
        @Query("pageLimit") pageLimit: Int,
        @Query("page") page: Int,
        @Query("strSearch") strSearch: String
    ): Observable<ListResponse<List<ContactResponse?>?>?>?

    @GET("api/app-call-center/tickets")
    fun getListNotes(
        @Query("pageLimit") pageLimit: Int,
        @Query("page") page: Int,
        @Query("strSearch") strSearch: String,
        @Query("search[category_id]") categoryId: String? = "",
        @Query("search[status_states]") state: String? = "",
        @Query("search[priority]") priority: String? = "",
    ): Observable<ListResponse<List<NoteResponse?>?>?>?

    @GET("api/app-call-center/call-history")
    fun getListHistory(
        @Query("pageLimit") pageLimit: Int,
        @Query("page") page: Int,
        @Query("strSearch") strSearch: String
    ): Observable<ListResponse<List<HistoryResponse?>?>?>?

    @GET("api/app-call-center/users")
    fun getListInternal(
        @Query("pageLimit") pageLimit: Int,
        @Query("page") page: Int,
        @Query("strSearch") strSearch: String
    ): Observable<ListResponse<List<InternalResponse?>?>?>?

    @POST("api/app-call-center/add-call-note")
    fun addCallNote(@Body callNoteRequest: CallNoteRequest): Observable<DataResponse<Any>>

    @POST("api/app-call-center/contact")
    fun addContact(@Body contactRequest: ContactRequest): Observable<DataResponse<AddContactResponse>>

    @GET("api/app-call-center/contact-by-phone/{phone}")
    fun getContactByPhone(@Path("phone") phoneNumber: String): Observable<DataResponse<ContactByPhoneResponse>>

    @GET("api/app-call-center/ticket-status")
    fun getStateList(): Observable<DataResponse<List<StateResponse?>?>?>?

    @GET("api/app-call-center/ticket-category")
    fun getTypeList(): Observable<DataResponse<List<TypeResponse?>?>?>?

    @GET("api/app-call-center/ticket-priority")
    fun getPriorityList(): Observable<DataResponse<List<PriorityResponse?>?>?>?

    @POST("api/app-call-center/tickets")
    fun addNote(@Body callNoteRequest: AddNoteRequest): Observable<DataResponse<NoteResponse>>
}