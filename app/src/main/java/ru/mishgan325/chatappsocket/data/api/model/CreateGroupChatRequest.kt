package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class CreateGroupChatRequest(
    @SerializedName("name") val name: String,
    @SerializedName("memberIds") val memberIds: List<Long>
)