package ru.mishgan325.chatappsocket.dto

import com.google.gson.annotations.SerializedName
import ru.mishgan325.chatappsocket.domain.models.Chat

data class ChatRoomDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String
) {
    fun toChat(): Chat {
        return Chat(
            id = this.id,
            name = this.name,
            type = this.type
        )
    }
}