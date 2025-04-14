package ru.mishgan325.chatappsocket.dto

import com.google.gson.annotations.SerializedName
import ru.mishgan325.chatappsocket.models.Chat
import ru.mishgan325.chatappsocket.models.User

data class UserDto(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String
) {
    fun toUser(): User {
        return User(
            id = this.id,
            username = this.username
        )
    }
}