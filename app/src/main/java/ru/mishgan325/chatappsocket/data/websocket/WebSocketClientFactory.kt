package ru.mishgan325.chatappsocket.data.websocket

import ru.mishgan325.chatappsocket.di.modules.WebSocketBaseUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClientFactory @Inject constructor(
    @WebSocketBaseUrl private val baseUrl: String
) {
    fun create(jwtToken: String): WebSocketClient {
        return WebSocketClient(jwtToken, baseUrl)
    }
}
