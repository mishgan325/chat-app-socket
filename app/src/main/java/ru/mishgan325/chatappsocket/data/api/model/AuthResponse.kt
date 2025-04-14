package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String
)