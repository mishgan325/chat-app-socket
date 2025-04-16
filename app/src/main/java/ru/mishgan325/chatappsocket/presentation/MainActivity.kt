package ru.mishgan325.chatappsocket.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ru.mishgan325.chatappsocket.dto.TabBarItem
import ru.mishgan325.chatappsocket.presentation.components.TabView
import ru.mishgan325.chatappsocket.presentation.navigation.Screen
import ru.mishgan325.chatappsocket.presentation.screens.ChatListScreen
import ru.mishgan325.chatappsocket.presentation.screens.ChatScreen
import ru.mishgan325.chatappsocket.presentation.screens.CreateNewChatScreen
import ru.mishgan325.chatappsocket.presentation.screens.MainScreen
import ru.mishgan325.chatappsocket.presentation.screens.SettingsScreen
import ru.mishgan325.chatappsocket.presentation.screens.authorization.LoginScreen
import ru.mishgan325.chatappsocket.presentation.screens.authorization.RegisterScreen
import ru.mishgan325.chatappsocket.presentation.ui.theme.ChatappsocketTheme
import ru.mishgan325.chatappsocket.utils.SessionManager
import ru.mishgan325.chatappsocket.viewmodels.ChatListViewModel
import ru.mishgan325.chatappsocket.viewmodels.ChatViewModel
import ru.mishgan325.chatappsocket.viewmodels.CreateNewChatViewModel
import ru.mishgan325.chatappsocket.viewmodels.LoginViewModel
import ru.mishgan325.chatappsocket.viewmodels.RegisterViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val chatListViewModel: ChatListViewModel by viewModels()
    private val createNewChatViewModel: CreateNewChatViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        val startDestination = if (sessionManager.isLoggedIn()) Screen.Chats.route else Screen.Login.route

        setContent {
            ChatappsocketTheme {
                MainScreen(startDestination)
            }
        }
    }
}