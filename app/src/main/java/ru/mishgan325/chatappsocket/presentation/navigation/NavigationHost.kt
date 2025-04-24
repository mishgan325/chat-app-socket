package ru.mishgan325.chatappsocket.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.mishgan325.chatappsocket.presentation.screens.ChatListScreen
import ru.mishgan325.chatappsocket.presentation.screens.ChatScreen
import ru.mishgan325.chatappsocket.presentation.screens.CreateNewChatScreen
import ru.mishgan325.chatappsocket.presentation.screens.SettingsScreen
import ru.mishgan325.chatappsocket.presentation.screens.authorization.LoginScreen
import ru.mishgan325.chatappsocket.presentation.screens.authorization.RegisterScreen
import ru.mishgan325.chatappsocket.viewmodels.ChatListViewModel
import ru.mishgan325.chatappsocket.viewmodels.ChatViewModel
import ru.mishgan325.chatappsocket.viewmodels.CreateNewChatViewModel
import ru.mishgan325.chatappsocket.viewmodels.LoginViewModel
import ru.mishgan325.chatappsocket.viewmodels.RegisterViewModel
import ru.mishgan325.chatappsocket.viewmodels.SettingsViewModel

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val chatListViewModel: ChatListViewModel = viewModel()
    val createNewChatViewModel: CreateNewChatViewModel = viewModel()
    val chatViewModel: ChatViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            if (initialState.destination.route == Screen.Chats.route &&
                targetState.destination.route == Screen.Chat.route) {
                slideInHorizontally(
                    initialOffsetX = { it }, // с правой стороны
                    animationSpec = tween(300)
                )
            } else {
                fadeIn(animationSpec = tween(300))
            }
        },
        exitTransition = {
            if (initialState.destination.route == Screen.Chats.route &&
                targetState.destination.route == Screen.Chat.route) {
                slideOutHorizontally(
                    targetOffsetX = { -it }, // уходит влево
                    animationSpec = tween(300)
                )
            } else {
                fadeOut(animationSpec = tween(300))
            }
        },
        popEnterTransition = {
            if (initialState.destination.route == Screen.Chat.route &&
                targetState.destination.route == Screen.Chats.route) {
                slideInHorizontally(
                    initialOffsetX = { -it }, // с левой стороны
                    animationSpec = tween(300)
                )
            } else {
                fadeIn(animationSpec = tween(300))
            }
        },
        popExitTransition = {
            if (initialState.destination.route == Screen.Chats.route &&
                targetState.destination.route == Screen.Chat.route) {
                slideOutHorizontally(
                    targetOffsetX = { -it }, // уходит влево
                    animationSpec = tween(300)
                )
            } else {
                fadeOut(animationSpec = tween(300))
            }
        }
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
            SettingsScreen(navController, settingsViewModel)
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
            ChatScreen(chatRoomId, chatName, chatViewModel)
        }
    }
}
