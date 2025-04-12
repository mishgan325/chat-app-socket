package ru.mishgan325.chatappsocket.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mishgan325.chatappsocket.dto.ChatMessageDto
import ru.mishgan325.chatappsocket.dto.ChatRoomDto
import ru.mishgan325.chatappsocket.dto.UserDto

interface ApiInterface {
    @POST("auth/sign-in")
    suspend fun login(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST("auth/sign-up")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("/auth/who-am-i")
    suspend fun whoami(): Response<WhoamiResponse>

    @GET("api/chat-rooms/my")
    suspend fun getMyChatRooms(): Response<List<ChatRoomDto>>

    @GET("api/users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("api/chat-messages/{chat_id}")
    suspend fun getChatMessages(
        @Path("chat_id") chatId: String
    ): Response<List<ChatMessageDto>>

    @POST("api/chat-rooms/private")
    suspend fun createPrivateChat(@Body request: CreatePrivateChatRequest): Response<Unit>

    @POST("api/chat-rooms/group")
    suspend fun createGroupChat(@Body request: CreateGroupChatRequest): Response<Unit>

    @GET("api/chat-messages/{chatRoomId}")
    suspend fun getChatMessagesWithPagination(
        @Path("chatRoomId") chatRoomId: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<List<ChatMessageDto>>
}