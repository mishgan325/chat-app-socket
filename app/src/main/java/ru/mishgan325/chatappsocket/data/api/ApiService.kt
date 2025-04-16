package ru.mishgan325.chatappsocket.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mishgan325.chatappsocket.data.api.model.AuthRequest
import ru.mishgan325.chatappsocket.data.api.model.AuthResponse
import ru.mishgan325.chatappsocket.data.api.model.ChatMessagesResponse
import ru.mishgan325.chatappsocket.data.api.model.CreateGroupChatRequest
import ru.mishgan325.chatappsocket.data.api.model.CreatePrivateChatRequest
import ru.mishgan325.chatappsocket.data.api.model.GetFileLinkResponse
import ru.mishgan325.chatappsocket.data.api.model.RegisterRequest
import ru.mishgan325.chatappsocket.data.api.model.RegisterResponse
import ru.mishgan325.chatappsocket.data.api.model.WhoamiResponse
import ru.mishgan325.chatappsocket.dto.ChatRoomDto
import ru.mishgan325.chatappsocket.dto.UserDto

interface ApiService {
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

    @POST("api/chat-rooms/private")
    suspend fun createPrivateChat(@Body request: CreatePrivateChatRequest): Response<Unit>

    @POST("api/chat-rooms/group")
    suspend fun createGroupChat(@Body request: CreateGroupChatRequest): Response<Unit>

    @GET("api/chat-messages/{chatRoomId}")
    suspend fun getChatMessages(
        @Path("chatRoomId") chatRoomId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Response<ChatMessagesResponse>

    @GET("api/files/{fileUrl}")
    suspend fun getFileLink(
        @Path("fileUrl") fileUrl: String
    ): Response<GetFileLinkResponse>
}