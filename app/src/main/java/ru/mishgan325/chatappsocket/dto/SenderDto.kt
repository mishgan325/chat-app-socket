package ru.mishgan325.chatappsocket.dto

import com.google.gson.annotations.SerializedName

data class SenderDto(
        @SerializedName("id") val id: Long,
        @SerializedName("username") val username: String
    )