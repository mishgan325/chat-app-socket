package ru.mishgan325.chatappsocket.data.websocket

import ru.mishgan325.chatappsocket.di.modules.WebSocketBaseUrl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketClientFactory @Inject constructor(
    @WebSocketBaseUrl private val baseUrl: String
) {
    fun create(jwtToken: String, chatId: Long): WebSocketClient {
        return WebSocketClient(jwtToken, chatId, baseUrl)
    }
}
