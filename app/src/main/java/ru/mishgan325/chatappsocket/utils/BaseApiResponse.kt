package ru.mishgan325.chatappsocket.utils

import retrofit2.Response

abstract class BaseApiResponse {

    suspend fun <T> safeApiCall(api: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = api()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    // Если успешный ответ и тело null — значит, это 204 No Content (или аналог)
                    @Suppress("UNCHECKED_CAST")
                    NetworkResult.Success(Unit as T) // Безопасно, если T == Unit
                }
            } else {
                errorMessage("${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            errorMessage(e.message.toString())
        }
    }

    private fun <T> errorMessage(e: String): NetworkResult.Error<T> =
        NetworkResult.Error(data = null, message = "Api call failed: ${e}")
}