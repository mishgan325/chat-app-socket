package ru.mishgan325.chatappsocket.data.websocket

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import ru.mishgan325.chatappsocket.data.websocket.model.ChatMessageWs
import ru.mishgan325.chatappsocket.data.websocket.model.StompFrame
import java.util.concurrent.TimeUnit
import kotlin.apply
import kotlin.let

class WebSocketClient(
    private val jwtToken: String,
    private val baseUrl: String
) {
    private val TAG = "WebSocketChatClient"
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .writeTimeout(3, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    fun connect() {
        val request = Request.Builder()
            .url(baseUrl) // Защищенное соединение
            .addHeader("Authorization", "Bearer $jwtToken")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket connected")
                sendStompConnect()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "📦 Raw STOMP message: $text")
                try {
                    val stompFrame = parseStompFrame(text)
                    if (stompFrame.command == "MESSAGE") {
                        val message = gson.fromJson(stompFrame.body, ChatMessageWs::class.java)
                        Log.d(TAG, "💥 WebSocketClient получил сообщение: $message")
                        onMessageReceived?.invoke(message)
                    }
                    else if (stompFrame.command == "CONNECTED") {
                        Log.d(TAG, "💥 WebSocketClient успешно подключен")
                    }
                    else if (stompFrame.command == "SUBSCRIBED") {
                        Log.d(TAG, "💥 WebSocketClient успешно подписался на топик")
                    }
                    else if (stompFrame.command == "UNSUBSCRIBED") {
                        Log.d(TAG, "💥 WebSocketClient успешно отписался от топик")
                    }
                    else if (stompFrame.command == "DISCONNECTED") {
                        Log.d(TAG, "💥 WebSocketClient успешно отсоединился")
                    }
                    else {
                        Log.d(TAG, stompFrame.toString())
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Ошибка парсинга STOMP-сообщения", e)
                }
            }



            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Received binary message: ${bytes.hex()}")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket error: ${t.message}", t)
                response?.let {
                    Log.e(TAG, "Response code: ${it.code}")
                }
            }
        })
    }


    fun parseStompFrame(raw: String): StompFrame {
        val cleaned = raw.trimEnd('\u0000')

        val lines = cleaned.lines()
        val command = lines.first()
        val headers = mutableMapOf<String, String>()
        var bodyIndex = lines.indexOfFirst { it.isBlank() }

        if (bodyIndex == -1) bodyIndex = lines.size // fallback

        for (i in 1 until bodyIndex) {
            val headerLine = lines[i]
            val (key, value) = headerLine.split(":", limit = 2)
            headers[key.trim()] = value.trim()
        }

        val body = lines.drop(bodyIndex + 1).joinToString("\n")
        return StompFrame(command, headers, body)
    }


    private fun sendStompConnect() {
        val connectFrame = buildString {
            append("CONNECT\n")
            append("accept-version:1.1\n")
            append("heart-beat:10000,10000\n")
            append("Authorization:Bearer $jwtToken\n")
            append("\n")
            append("\u0000")
        }

        Log.d(TAG, "Sending STOMP CONNECT frame:\n$connectFrame")

        webSocket?.send(connectFrame)
    }


    fun subscribe(chatId: Long) {
        val subscribeFrame = buildString {
            append("SUBSCRIBE\n")
            append("id:sub-$chatId\n")
            append("destination:/topic/chat/$chatId\n")
            append("\n")
            append("\u0000")
        }
        Log.d(TAG, "Sending STOMP SUBSCRIBE frame:\n$subscribeFrame")
        webSocket?.send(subscribeFrame)
    }

    fun unsubscribe(chatId: Long) {
        val unsubscribeFrame = buildString {
            append("UNSUBSCRIBE\n")
            append("id:sub-$chatId\n")
            append("\n")
            append("\u0000")
        }
        Log.d(TAG, "Sending STOMP UNSUBSCRIBE frame:\n$unsubscribeFrame")
        webSocket?.send(unsubscribeFrame)
    }



    fun sendMessage(content: String, fileUrl: String, chatId: Long) {
        val message = ChatMessageWs(
            content = content,
            fileUrl = fileUrl,
            chatId = chatId
        )
        sendMessageStompFrame(message)
    }

    private fun sendMessageStompFrame(message: ChatMessageWs) {
        val json = gson.toJson(message)
        val stompFrame = buildString {
            append("SEND\n")
            append("destination:/app/chat.sendMessage\n")
            append("content-type:application/json\n")
            append("\n")
            append(json)
            append("\u0000")
        }

        webSocket?.send(stompFrame)
    }

    private var onMessageReceived: ((ChatMessageWs) -> Unit)? = null

    fun setOnMessageReceivedListener(listener: (ChatMessageWs) -> Unit) {
        this.onMessageReceived = listener
    }

    fun disconnect() {
        try {
            val disconnectFrame = "DISCONNECT\n\n\u0000"
            webSocket?.send(disconnectFrame)

            webSocket?.close(1000, "Normal closure")
        } finally {
            client.dispatcher.executorService.shutdown()
            webSocket = null
        }
    }

}
