package ru.mishgan325.chatappsocket.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import ru.mishgan325.chatappsocket.data.websocket.model.ChatMessageWs
import javax.inject.Inject

class ObserveIncomingMessagesUseCase @Inject constructor(
    private val webSocketService: WebSocketService
) {
    fun invoke(): Flow<ChatMessageWs> = webSocketService.incomingMessages
}
