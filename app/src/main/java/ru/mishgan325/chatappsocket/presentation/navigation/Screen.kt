package ru.mishgan325.chatappsocket.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("Login")
    object Register : Screen("Register")
    object Chats : Screen("Chats")
    object Settings : Screen("Settings")
    object CreateNewChat : Screen("New chat")
}
