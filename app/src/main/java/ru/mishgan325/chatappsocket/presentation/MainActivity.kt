package ru.mishgan325.chatappsocket.presentation

//import ru.mishgan325.chatappsocket.presentation.screens.RegisterScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.mishgan325.chatappsocket.presentation.screens.ChatListScreen
import ru.mishgan325.chatappsocket.presentation.screens.LoginScreen
import ru.mishgan325.chatappsocket.presentation.screens.RegisterScreen
import ru.mishgan325.chatappsocket.presentation.screens.SelectUserScreen
import ru.mishgan325.chatappsocket.presentation.ui.theme.ChatappsocketTheme
import ru.mishgan325.chatappsocket.utils.SessionManager
import ru.mishgan325.chatappsocket.viewmodels.LoginViewModel
import ru.mishgan325.chatappsocket.viewmodels.RegisterViewModel
import ru.mishgan325.chatappsocket.viewmodels.ChatListViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val chatListViewModel: ChatListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        val startDestination = if (sessionManager.isLoggedIn()) {
            "chat_select"
        } else {
            "login"
        }

        setContent {
            ChatappsocketTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("login") {
                        LoginScreen(
                            navController, loginViewModel
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            navController, registerViewModel
                        )
                    }

                    composable("chat_select") {
                        ChatListScreen(
                            navController, chatListViewModel
                        )
                    }

                    composable("settings") {
                        Text(text = "Chat settings screen будет здесь")
                    }

                    composable("select_users_new_chat") {
                        Text(text = "New chat users select screen будет здесь")
                    }
                }
            }
        }
    }
}
