package ru.mishgan325.chatappsocket.data

import okhttp3.Response
import ru.mishgan325.chatappsocket.data.api.RemoteDataSource
import ru.mishgan325.chatappsocket.data.api.model.AuthRequest
import ru.mishgan325.chatappsocket.data.api.model.AuthResponse
import ru.mishgan325.chatappsocket.data.api.model.ChatRoomDtoResponse
import ru.mishgan325.chatappsocket.data.api.model.RegisterRequest
import ru.mishgan325.chatappsocket.data.api.model.RegisterResponse
import ru.mishgan325.chatappsocket.dto.ChatRoomDto
import ru.mishgan325.chatappsocket.viewmodels.BaseApiResponse
import ru.mishgan325.chatappsocket.viewmodels.NetworkResult
import javax.inject.Inject


class ApiRepository @Inject constructor(
//    private val sessionManager: SessionManager
    private val remoteDataSource: RemoteDataSource
): BaseApiResponse() {
//    suspend fun login(username: String, password: String): Result<Unit> = try {
//        val response = api.login(AuthRequest(username, password))
//        if (response.isSuccessful) {
//            response.body()?.let {
//                sessionManager.saveAuthToken(it.token)
//                Result.success(Unit)
//            } ?: Result.failure(Exception("Empty response"))
//        } else {
//            Result.failure(Exception("Login failed: ${response.code()}"))
//        }
//    } catch (e: Exception) {
//        Result.failure(e)
//    }

//    suspend fun register(email: String, username: String, password: String): Result<Unit> {
//        // Аналогичная логика для регистрации
//    }
    suspend fun login(username: String, password: String): NetworkResult<AuthResponse> {
        return safeApiCall { remoteDataSource.login(AuthRequest(username, password)) }
    }

    suspend fun register(email: String, username: String, password: String): NetworkResult<RegisterResponse> {
        return safeApiCall { remoteDataSource.register(RegisterRequest(email, username, password)) }
    }

    suspend fun getMyChatRooms(): NetworkResult<List<ChatRoomDto>> {
        return safeApiCall { remoteDataSource.getMyChatRooms() }
    }

//    suspend fun
//
//    suspend fun getAllPosts(): NetworkResult<List<PostResponse>> {
//        return safeApiCall { remoteDataSource.getAllPosts() }
//    }

//    suspend fun postPost(body: PostResponse): NetworkResult<PostResponse> {
//        return safeApiCall { remoteDataSource.postPosts(body = body) }
//    }
}