package ru.mishgan325.chatappsocket.api

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String
)


data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterResponse(
    @SerializedName("token") val token: String
)


data class WhoamiResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String
)


data class CreatePrivateChatRequest(
    @SerializedName("name") val name: String,
    @SerializedName("user_id") val user_id: Long
)


data class CreateGroupChatRequest(
    @SerializedName("name") val name: String,
    @SerializedName("memberIds") val memberIds: List<Long>
)


data class AddUserRequest(
    @SerializedName("user_id") val user_id: Long
)