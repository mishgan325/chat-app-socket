package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class SortX(
        @SerializedName("empty") val empty: Boolean,
        @SerializedName("sorted") val sorted: Boolean,
        @SerializedName("unsorted") val unsorted: Boolean
    )