package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val webSocketService: WebSocketService
) {

    suspend fun invoke(content: String, fileUrl: String, chatId: Long) = webSocketService.sendMessage(content, fileUrl, chatId)
}
