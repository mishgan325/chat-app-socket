package ru.mishgan325.chatappsocket.data.api

import ru.mishgan325.chatappsocket.data.api.model.AuthRequest
import ru.mishgan325.chatappsocket.data.api.model.CreateGroupChatRequest
import ru.mishgan325.chatappsocket.data.api.model.CreatePrivateChatRequest
import ru.mishgan325.chatappsocket.data.api.model.RegisterRequest
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    suspend fun register(registerRequest: RegisterRequest) = apiService.register(registerRequest)
    suspend fun login(authRequest: AuthRequest) = apiService.login(authRequest)

    suspend fun whoami() = apiService.whoami()

    suspend fun getUsers(username: String? = null) = apiService.getUsers(username)

    suspend fun getMyChatRooms() = apiService.getMyChatRooms()

    suspend fun createPrivateChat(createPrivateChatRequest: CreatePrivateChatRequest) =
        apiService.createPrivateChat(createPrivateChatRequest)

    suspend fun createGroupChat(createGroupChatRequest: CreateGroupChatRequest) =
        apiService.createGroupChat(createGroupChatRequest)

    suspend fun getChatMessages(
        chatRoomId: Long,
        page: Int,
        size: Int,
        sort: String
    ) = apiService.getChatMessages(chatRoomId, page, size, sort)

    suspend fun getFileLink(fileUrl: String) = apiService.getFileLink(fileUrl)

    suspend fun deleteMessage(chatMessageId: Long) = apiService.deleteMessage(chatMessageId)
}