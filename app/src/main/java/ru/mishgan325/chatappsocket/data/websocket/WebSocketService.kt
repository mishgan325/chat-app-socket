package ru.mishgan325.chatappsocket.data.websocket

import android.util.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import ru.mishgan325.chatappsocket.data.websocket.model.ChatMessageWs
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketService @Inject constructor(
    private val webSocketClientFactory: WebSocketClientFactory,
    private val sessionManager: SessionManager
) {
    private val TAG = "WebSocketService"
    private var webSocketClient: WebSocketClient? = null

    private val _incomingMessages = MutableSharedFlow<ChatMessageWs>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    private var isConnected = false // Флаг, который отслеживает состояние подключения

    // Соединение
    fun connect() {
        // Проверяем, если уже подключены, то не подключаемся снова
        if (isConnected) {
            Log.d(TAG, "Already connected, skipping connection.")
            return
        }

        webSocketClient = webSocketClientFactory.create(sessionManager.getAuthToken().toString()).apply {
            setOnMessageReceivedListener { message ->
                Log.d(TAG, "💥 WebSocketService получил сообщение: $message")
                val emitted = _incomingMessages.tryEmit(message)
                Log.d(TAG, "🔥 Эмит в поток: $emitted")
            }

            connect() // подключаемся
        }

        // Помечаем, что подключение успешно
        isConnected = true
    }

    fun subscribe(chatId: Long) {
        webSocketClient?.subscribe(chatId)
        Log.d(TAG, "Подписка на чат $chatId выполнена")
    }

    fun unsubscribe(chatId: Long) {
        webSocketClient?.unsubscribe(chatId)
        Log.d(TAG, "Отписка от чата $chatId выполнена")
    }

    suspend fun sendMessage(content: String, fileUrl: String, chatId: Long) {
        try {
            if (webSocketClient == null) {
                connect()
            }
            webSocketClient?.sendMessage(content, fileUrl, chatId)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}")
        }
    }


    // Отключение
    fun disconnect() {
        webSocketClient?.disconnect()
//        webSocketClient = null
        isConnected = false
        Log.d(TAG, "WebSocket отключён и обнулён")
    }
}