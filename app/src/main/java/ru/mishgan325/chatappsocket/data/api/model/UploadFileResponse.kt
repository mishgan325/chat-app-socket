package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class UploadFileResponse(
    @SerializedName("file_path") val filePath: String
)