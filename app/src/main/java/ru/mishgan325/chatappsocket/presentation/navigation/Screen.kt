package ru.mishgan325.chatappsocket.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Login : Screen("Login")
    object Register : Screen("Register")
    object Chats : Screen("Chats")
    object Settings : Screen("Settings")
    object CreateNewChat : Screen("New chat")
    object Chat : Screen("chat/{chatRoomId}/{chatName}") {
        fun withArgs(chatRoomId: String, chatName: String): String =
            "chat/$chatRoomId/${Uri.encode(chatName)}"
    }
}
