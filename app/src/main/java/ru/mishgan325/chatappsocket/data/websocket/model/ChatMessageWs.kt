package ru.mishgan325.chatappsocket.data.websocket.model

data class ChatMessageWs(
    val chatMessageId: Long = -1,
    val content: String,
    val sender: String = "",
    val chatId: Long = -1,
    val fileUrl: String = "",
    val timestamp: String = ""
)
