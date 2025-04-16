package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class CreateGroupChatRequest(
    @SerializedName("name") val name: String,
    @SerializedName("member_ids") val memberIds: List<Long>
)