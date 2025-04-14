package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName


data class AddUserRequest(
    @SerializedName("user_id") val user_id: Long
)