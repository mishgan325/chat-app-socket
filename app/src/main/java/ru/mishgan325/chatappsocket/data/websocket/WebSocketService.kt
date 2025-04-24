package ru.mishgan325.chatappsocket.data.websocket

import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.mishgan325.chatappsocket.data.websocket.model.ChatMessageWs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketService @Inject constructor(
    private val webSocketClientFactory: WebSocketClientFactory
) {
    private val TAG = "WebSocketService"
    private var webSocketClient: WebSocketClient? = null

    private val _incomingMessages = MutableSharedFlow<ChatMessageWs>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    // Соединение
    fun connect(jwtToken: String, chatId: Long) {
        webSocketClient = webSocketClientFactory.create(jwtToken, chatId).apply {
            setOnMessageReceivedListener { message ->
                Log.d(TAG, "💥 WebSocketClient получил сообщение: $message")
                val emitted = _incomingMessages.tryEmit(message)
                Log.d(TAG, "🔥 Эмит в поток: $emitted")
            }

            connect()
        }
    }

    // Отправка сообщения (сделано асинхронным)
    suspend fun sendMessage(content: String, fileUrl: String, chatId: Long) {
        try {
            webSocketClient?.sendMessage(content, fileUrl, chatId)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}")
            // Тут можно добавить дополнительную логику обработки ошибок, например, повторная попытка
        }
    }

    // Отключение
    fun disconnect() {
        webSocketClient?.disconnect()
    }
}
