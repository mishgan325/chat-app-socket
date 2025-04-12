package ru.mishgan325.chatappsocket.api

import ru.mishgan325.chatappsocket.utils.SessionManager
import ru.mishgan325.chatappsocket.R

class ApiRepository(
    private val api: ApiInterface,
    private val sessionManager: SessionManager
) {
    suspend fun login(username: String, password: String): Result<Unit> = try {
        val response = api.login(AuthRequest(username, password))
        if (response.isSuccessful) {
            response.body()?.let {
                sessionManager.saveAuthToken(it.token)
                Result.success(Unit)
            } ?: Result.failure(Exception("Empty response"))
        } else {
            Result.failure(Exception("Login failed: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

//    suspend fun register(email: String, username: String, password: String): Result<Unit> {
//        // Аналогичная логика для регистрации
//    }
}