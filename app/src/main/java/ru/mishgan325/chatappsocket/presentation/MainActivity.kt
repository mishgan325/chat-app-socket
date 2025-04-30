package ru.mishgan325.chatappsocket.presentation

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
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
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), LifecycleEventObserver {

    @Inject
    lateinit var webSocketService: WebSocketService

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // Подписка на жизненный цикл приложения
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        enableEdgeToEdge()

        val startDestination = if (sessionManager.isLoggedIn()) Screen.Chats.route else Screen.Login.route

        setContent {
            ChatappsocketTheme {
                MainScreen(startDestination)
            }
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                if (sessionManager.isLoggedIn()) {
                    webSocketService.connect()
                }
            }
            Lifecycle.Event.ON_STOP -> {
                webSocketService.disconnect()
            }
            else -> {}
        }

        when (event) {
            Lifecycle.Event.ON_CREATE -> Log.d("Lifecycle", "Создано")
            Lifecycle.Event.ON_START -> Log.d("Lifecycle", "Старт")
            Lifecycle.Event.ON_RESUME -> Log.d("Lifecycle", "Резюмировано")
            Lifecycle.Event.ON_PAUSE -> Log.d("Lifecycle", "Пауза")
            Lifecycle.Event.ON_STOP -> Log.d("Lifecycle", "Остановлено")
            Lifecycle.Event.ON_DESTROY -> Log.d("Lifecycle", "Уничтожено")
            Lifecycle.Event.ON_ANY -> Log.d("Lifecycle", "Любое событие")
        }
    }
}
