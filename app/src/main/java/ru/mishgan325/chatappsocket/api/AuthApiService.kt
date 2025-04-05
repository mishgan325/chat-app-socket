package ru.mishgan325.chatappsocket.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/sign-in")
    suspend fun login(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST("auth/sign-up")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>
}
