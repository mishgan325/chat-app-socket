package ru.mishgan325.chatappsocket.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Chats : Screen("chats")
    object Settings : Screen("settings")
    object CreateNewChat : Screen("create_new_chat")
    object Chat : Screen("chat/{chatRoomId}/{chatName}/{isPrivate}") {
        fun withArgs(chatRoomId: Long, chatName: String, isPrivate: Boolean): String =
            "chat/$chatRoomId/${Uri.encode(chatName)}/$isPrivate"
    }
}
