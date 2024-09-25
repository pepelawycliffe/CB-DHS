package com.example.cb_dhs.api

import com.example.cb_dhs.model.AuthResponse
import com.example.cb_dhs.model.LoginRequest
import com.example.cb_dhs.model.RegisterRequest
import com.example.cb_dhs.model.StatusResponse
import com.example.cb_dhs.model.UserInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface AuthApi {

    @POST("api/v1/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<AuthResponse>


    @POST("api/v1/login")
    fun login(
        @Body request: LoginRequest,
    ): Call<AuthResponse>


    @GET("api/v1/me")
    fun getUserInfo(@Header("Cookie") token: String?): Call<UserInfoResponse>

    @GET("api/v1/logout")
    fun logout(): Call<StatusResponse>

}