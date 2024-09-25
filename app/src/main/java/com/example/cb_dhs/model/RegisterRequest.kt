package com.example.cb_dhs.model

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("avatar") val avatar: String
)