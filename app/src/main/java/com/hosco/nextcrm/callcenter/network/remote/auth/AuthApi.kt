package com.hosco.nextcrm.callcenter.network.remote.auth

import io.reactivex.Observable
import com.hosco.nextcrm.callcenter.network.remote.common.DataResponse
import retrofit2.http.*

interface AuthApi {

    @POST("api/app-mobile/login")
    fun login(@Body loginRequest: AuthRequest?): Observable<DataResponse<AuthResponse?>?>?

    @GET("api/app-mobile/validate-tenant/{Ma_Khach_Hang}")
    fun checkExistCustomer(@Path("Ma_Khach_Hang") tenantCode: String): Observable<DataResponse<CustommerResponse?>?>?
}