package ru.mishgan325.chatappsocket.data

import ru.mishgan325.chatappsocket.data.api.RemoteDataSource
import ru.mishgan325.chatappsocket.data.api.model.AuthRequest
import ru.mishgan325.chatappsocket.data.api.model.AuthResponse
import ru.mishgan325.chatappsocket.data.api.model.ChatMessagesResponse
import ru.mishgan325.chatappsocket.data.api.model.CreateGroupChatRequest
import ru.mishgan325.chatappsocket.data.api.model.CreatePrivateChatRequest
import ru.mishgan325.chatappsocket.data.api.model.RegisterRequest
import ru.mishgan325.chatappsocket.data.api.model.RegisterResponse
import ru.mishgan325.chatappsocket.data.api.model.WhoamiResponse
import ru.mishgan325.chatappsocket.dto.ChatRoomDto
import ru.mishgan325.chatappsocket.dto.UserDto
import ru.mishgan325.chatappsocket.utils.BaseApiResponse
import ru.mishgan325.chatappsocket.utils.NetworkResult
import javax.inject.Inject


class ApiRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : BaseApiResponse() {

    suspend fun login(username: String, password: String): NetworkResult<AuthResponse> {
        return safeApiCall { remoteDataSource.login(AuthRequest(username, password)) }
    }

    suspend fun register(
        email: String, username: String, password: String
    ): NetworkResult<RegisterResponse> {
        return safeApiCall { remoteDataSource.register(RegisterRequest(email, username, password)) }
    }

    suspend fun whoami(): NetworkResult<WhoamiResponse> {
        return safeApiCall { remoteDataSource.whoami() }
    }

    suspend fun getMyChatRooms(): NetworkResult<List<ChatRoomDto>> {
        return safeApiCall { remoteDataSource.getMyChatRooms() }
    }

    suspend fun getUsers(): NetworkResult<List<UserDto>> {
        return safeApiCall { remoteDataSource.getUsers() }
    }

    suspend fun createPrivateChat(name: String, userId: Long): NetworkResult<Unit> {
        return safeApiCall {
            remoteDataSource.createPrivateChat(
                CreatePrivateChatRequest(
                    name, userId
                )
            )
        }
    }

    suspend fun createGroupChat(name: String, memberIds: List<Long>): NetworkResult<Unit> {
        return safeApiCall {
            remoteDataSource.createGroupChat(
                CreateGroupChatRequest(
                    name, memberIds
                )
            )
        }
    }

    suspend fun getChatMessages(
        chatRoomId: Long, page: Int, size: Int, sort: String
    ): NetworkResult<ChatMessagesResponse> {
        return safeApiCall {
            remoteDataSource.getChatMessages(chatRoomId, page, size, sort)
        }
    }
}