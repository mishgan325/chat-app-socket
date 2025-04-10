package ru.mishgan325.chatappsocket.dto

import com.google.gson.annotations.SerializedName

data class ChatRoomDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String
)