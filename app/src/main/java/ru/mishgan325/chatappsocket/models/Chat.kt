package ru.mishgan325.chatappsocket.models

data class Chat(
    val id: String,
    val name: String,
    val type: String  // "private", "group" и т.д.
)