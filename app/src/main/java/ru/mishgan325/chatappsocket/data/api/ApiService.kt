package ru.mishgan325.chatappsocket.data.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.mishgan325.chatappsocket.data.api.model.AuthRequest
import ru.mishgan325.chatappsocket.data.api.model.AuthResponse
import ru.mishgan325.chatappsocket.data.api.model.ChatMessagesResponse
import ru.mishgan325.chatappsocket.data.api.model.CreateGroupChatRequest
import ru.mishgan325.chatappsocket.data.api.model.CreatePrivateChatRequest
import ru.mishgan325.chatappsocket.data.api.model.EditMessageRequest
import ru.mishgan325.chatappsocket.data.api.model.GetFileLinkResponse
import ru.mishgan325.chatappsocket.data.api.model.RegisterRequest
import ru.mishgan325.chatappsocket.data.api.model.RegisterResponse
import ru.mishgan325.chatappsocket.data.api.model.UploadFileResponse
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
    suspend fun getUsers(@Query("username") username: String? = null): Response<List<UserDto>>

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

    @DELETE("/api/chat-messages/{chatMessageId}")
    suspend fun deleteMessage(@Path("chatMessageId") chatMessageId: Long): Response<Unit>

    @PUT("/api/chat-messages/{chatMessageId}")
    suspend fun editMessage(
        @Path("chatMessageId") chatMessageId: Long,
        @Body request: EditMessageRequest
    ): Response<Unit>

    @POST("api/chat-rooms/{chatId}/add-user")
    suspend fun addUserToChat(
        @Path("chatId") chatId: Long,
        @Query("userId") userId: Long
    ): Response<Unit>

    @Multipart
    @POST("api/files/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<UploadFileResponse>
}