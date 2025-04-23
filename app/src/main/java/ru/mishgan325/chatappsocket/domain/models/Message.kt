package ru.mishgan325.chatappsocket.domain.models

data class Message(
    val id: Long,
    val sender: Sender,
    val content: String,
    val fileUrl: String,
    val timestamp: String,
    val isMine: Boolean
)