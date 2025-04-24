package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import javax.inject.Inject

class UnsubscribeWebSocketUseCase @Inject constructor(
    private val webSocketService: WebSocketService
) {
    fun invoke(chatId: Long) {
        webSocketService.unsubscribe(chatId)
    }
}
