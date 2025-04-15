package ru.mishgan325.chatappsocket.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
        val startDestination = if (sessionManager.isLoggedIn()) {
            Screen.Chats.route
        } else {
            Screen.Login.route
        }

        setContent {
            ChatappsocketTheme {
                val navController = rememberNavController()
                val currentDestination by navController.currentBackStackEntryFlow.collectAsState(
                    initial = navController.currentBackStackEntry
                )

                val homeTab = TabBarItem(
                    title = Screen.Chats.route,
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home
                )
                val settingsTab = TabBarItem(
                    title = Screen.Settings.route,
                    selectedIcon = Icons.Filled.Settings,
                    unselectedIcon = Icons.Outlined.Settings
                )
                val tabBarItems = listOf(homeTab, settingsTab)

                val bottomBarRoutes = listOf(Screen.Chats.route, Screen.Settings.route)

                Scaffold(
                    bottomBar = {
                        val currentRoute = currentDestination?.destination?.route
                        if (currentRoute in bottomBarRoutes) {
                            TabView(tabBarItems, navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController, loginViewModel)
                        }

                        composable(Screen.Register.route) {
                            RegisterScreen(navController, registerViewModel)
                        }

                        composable(Screen.Chats.route) {
                            ChatListScreen(navController, chatListViewModel)
                        }

                        composable(Screen.Settings.route) {
                            SettingsScreen(navController)
                        }

                        composable(Screen.CreateNewChat.route) {
                            CreateNewChatScreen(navController, createNewChatViewModel)
                        }

                        composable(
                            route = Screen.Chat.route,
                            arguments = listOf(
                                navArgument("chatRoomId") { type = NavType.StringType },
                                navArgument("chatName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val chatRoomId = backStackEntry.arguments?.getString("chatRoomId")?.toLongOrNull() ?: 0L
                            val chatName = backStackEntry.arguments?.getString("chatName") ?: ""
                            ChatScreen(
                                chatRoomId = chatRoomId,
                                chatName = chatName,
                                viewModel = chatViewModel
                            )
                        }

                    }
                }
            }
        }
    }
}
