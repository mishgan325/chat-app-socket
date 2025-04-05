package ru.mishgan325.chatappsocket.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.mishgan325.chatappsocket.dto.ChatMessageDto
import ru.mishgan325.chatappsocket.dto.ChatRoomDto

interface ApiService {
    @POST("auth/sign-in")
    suspend fun login(@Body authRequest: AuthRequest): Response<AuthResponse>

    @POST("auth/sign-up")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @GET("api/chat-rooms/my")
    suspend fun getMyChatRooms(): Response<List<ChatRoomDto>>

    @GET("api/chat-messages/{chat_id}")
    suspend fun getChatMessages(
        @Path("chat_id") chatId: String
    ): Response<List<ChatMessageDto>>

}
