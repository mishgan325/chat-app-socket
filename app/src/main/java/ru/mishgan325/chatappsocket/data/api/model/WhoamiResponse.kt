package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class WhoamiResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String
)