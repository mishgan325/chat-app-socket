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
    private val chatId: Long,
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
                subscribeToChat()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "📦 Raw STOMP message: $text")
                try {
                    val stompFrame = parseStompFrame(text)
                    val message = gson.fromJson(stompFrame.body, ChatMessageWs::class.java)
                    Log.d(TAG, "💥 WebSocketClient получил сообщение: $message")
                    onMessageReceived?.invoke(message)
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


    private fun subscribeToChat() {
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
        webSocket?.let {
            it.close(1000, "User initiated disconnect")
        }
        client.dispatcher.executorService.shutdown()
    }
}
