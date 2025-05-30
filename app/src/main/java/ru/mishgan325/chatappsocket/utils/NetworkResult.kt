package ru.mishgan325.chatappsocket.utils

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : NetworkResult<T>(data)
    class Error<T>(data: T?, message: String?) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
    class Idle<T> : NetworkResult<T>()
}
