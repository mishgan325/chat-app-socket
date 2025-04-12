package ru.mishgan325.chatappsocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.mishgan325.chatappsocket.api.ApiInterface
import ru.mishgan325.chatappsocket.screens.LoginScreen
import ru.mishgan325.chatappsocket.screens.RegisterScreen
import ru.mishgan325.chatappsocket.ui.theme.ChatappsocketTheme
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager

class MainActivity : ComponentActivity() {

    private lateinit var apiInterface: ApiInterface
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        apiInterface = ApiConfig.retrofit.create(ApiInterface::class.java)

        enableEdgeToEdge()
        setContent {
            ChatappsocketTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onRegisterClicked = {
                                navController.navigate("register")
                            },
                            apiInterface = apiInterface,
                            navHostController = navController
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            apiInterface = apiInterface,
                            navHostController = navController,
                            viewModel = TODO()
                        )
                    }

                    composable("chat_select") {
                        Text(text = "Chat select screen будет здесь")
//                        ChatSelectScreen()
                    }

                    composable("settings") {
                        Text(text = "Chat settings screen будет здесь")
//                        SettingsScreen()
                    }
                }
//                TestScreen()
            }
        }
    }
}