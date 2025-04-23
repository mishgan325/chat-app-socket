package ru.mishgan325.chatappsocket.data.websocket.model

data class ChatMessageWs(
    val id: Long,
    val content: String,
    val sender: String? = null,
    val chatId: Long,
    val fileUrl: String? = null
)
