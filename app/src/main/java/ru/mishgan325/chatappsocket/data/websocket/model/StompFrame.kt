package ru.mishgan325.chatappsocket.data.websocket.model

data class StompFrame(
    val command: String,
    val headers: Map<String, String>,
    val body: String
)
