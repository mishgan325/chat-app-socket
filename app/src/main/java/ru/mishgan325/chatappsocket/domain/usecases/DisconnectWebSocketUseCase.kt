package ru.mishgan325.chatappsocket.domain.usecases

import android.util.Log
import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import javax.inject.Inject

class DisconnectWebSocketUseCase @Inject constructor(
    private val webSocketService: WebSocketService
) {
    fun invoke() {
        webSocketService.disconnect()
        Log.d("DisconnectWebSocketUseCase", "Disconnected")
    }
}
