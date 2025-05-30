package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class ChatRoomDtoResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String
)