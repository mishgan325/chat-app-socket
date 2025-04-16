package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class CreatePrivateChatRequest(
    @SerializedName("name") val name: String,
    @SerializedName("user_id") val userId: Long
)