package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import javax.inject.Inject

class ConnectToWebSocketUseCase @Inject constructor(
    private val webSocketService: WebSocketService
) {
    fun invoke(jwtToken: String, chatId: Long) {
        webSocketService.connect(jwtToken, chatId)
    }
}
