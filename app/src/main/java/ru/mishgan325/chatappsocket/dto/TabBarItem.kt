package ru.mishgan325.chatappsocket.dto

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import ru.mishgan325.chatappsocket.presentation.navigation.Screen


sealed class TabBarItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Chats : TabBarItem(Screen.Chats.route, Icons.Filled.Home, Icons.Outlined.Home)
    object Settings :
        TabBarItem(Screen.Settings.route, Icons.Filled.Settings, Icons.Outlined.Settings)

    companion object {
        val items = listOf(Chats, Settings)
    }
}