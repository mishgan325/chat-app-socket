package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import javax.inject.Inject

class SubscribeWebSocketUseCase @Inject constructor(
    private val webSocketService: WebSocketService
) {
    fun invoke(chatId: Long) {
        webSocketService.subscribe(chatId)
    }
}
