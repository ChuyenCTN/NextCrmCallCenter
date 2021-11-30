package com.hosco.nextcrm.callcenter.network.remote.auth


interface AuthRepository {

    suspend fun login(authRequest: AuthRequest): AuthResponse
}