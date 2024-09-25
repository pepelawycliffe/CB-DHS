package com.example.cb_dhs.repository


import com.example.cb_dhs.api.AuthApi
import com.example.cb_dhs.model.LoginRequest
import com.example.cb_dhs.model.RegisterRequest
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val api: AuthApi
) {

    fun register(
        registerRequest: RegisterRequest
    ) = api.register(registerRequest)


    fun login(
        request: LoginRequest
    ) = api.login(request)

    fun getUserInfo(token: String) = api.getUserInfo(token)

    fun logout() = api.logout()

}
