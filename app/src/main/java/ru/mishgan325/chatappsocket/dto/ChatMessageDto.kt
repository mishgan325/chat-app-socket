package ru.mishgan325.chatappsocket.dto

import com.google.gson.annotations.SerializedName

data class ChatMessageDto(
    @SerializedName("id") val id: Long,
    @SerializedName("sender") val sender: SenderDto,
    @SerializedName("content") val content: String,
    @SerializedName("fileUrl") val fileUrl: String,
    @SerializedName("timestamp") val timestamp: String
) {
    data class SenderDto(
        @SerializedName("id") val id: Long,
        @SerializedName("username") val username: String
    )
}