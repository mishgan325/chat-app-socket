package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import javax.inject.Inject

class DisconnectWebSocketUseCase @Inject constructor(
    private val webSocketService: WebSocketService
) {
    fun invoke() {
        webSocketService.disconnect()
    }
}
