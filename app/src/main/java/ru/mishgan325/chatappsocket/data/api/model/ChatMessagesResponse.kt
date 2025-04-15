package ru.mishgan325.chatappsocket.data.api.model

import com.google.gson.annotations.SerializedName

data class ChatMessagesResponse(
    @SerializedName("content") val content: List<Content>,
    @SerializedName("empty") val empty: Boolean,
    @SerializedName("first") val first: Boolean,
    @SerializedName("last") val last: Boolean,
    @SerializedName("number") val number: Int,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("pageable") val pageable: Pageable,
    @SerializedName("size") val size: Int,
    @SerializedName("sort") val sort: SortX,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("totalPages") val totalPages: Int
) {
    data class Content(
        @SerializedName("content") val content: String,
        @SerializedName("fileUrl") val fileUrl: String,
        @SerializedName("id") val id: Int,
        @SerializedName("sender") val sender: Sender,
        @SerializedName("timestamp") val timestamp: String
    )

    data class Pageable(
        @SerializedName("offset") val offset: Int,
        @SerializedName("pageNumber") val pageNumber: Int,
        @SerializedName("pageSize") val pageSize: Int,
        @SerializedName("paged") val paged: Boolean,
        @SerializedName("sort") val sort: SortX,
        @SerializedName("unpaged") val unpaged: Boolean
    )

    data class Sender(
        @SerializedName("id") val id: Int,
        @SerializedName("username") val username: String
    )

    data class SortX(
        @SerializedName("empty") val empty: Boolean,
        @SerializedName("sorted") val sorted: Boolean,
        @SerializedName("unsorted") val unsorted: Boolean
    )
}
