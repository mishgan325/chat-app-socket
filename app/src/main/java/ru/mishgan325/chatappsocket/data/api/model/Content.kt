package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class Content(
        @SerializedName("content") val content: String,
        @SerializedName("fileUrl") val fileUrl: String,
        @SerializedName("id") val id: Long,
        @SerializedName("sender") val sender: Sender,
        @SerializedName("timestamp") val timestamp: String
    )