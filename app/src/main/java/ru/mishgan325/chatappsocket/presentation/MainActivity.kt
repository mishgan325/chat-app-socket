package ru.mishgan325.chatappsocket.presentation

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
import ru.mishgan325.chatappsocket.presentation.screens.LoginScreen
//import ru.mishgan325.chatappsocket.presentation.screens.RegisterScreen
import ru.mishgan325.chatappsocket.presentation.ui.theme.ChatappsocketTheme
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager
import ru.mishgan325.chatappsocket.viewmodels.LoginViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private lateinit var mainApi: MainApi
//    private lateinit var sessionManager: SessionManager
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        sessionManager = SessionManager(this)
//        mainApi = ApiConfig.retrofit.create(MainApi::class.java)

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
                            navHostController = navController,
                            loginViewModel = loginViewModel
                        )
                    }

                    composable("register") {
                        Text(text = "Register screen будет здесь")
//                        RegisterScreen(
////                            mainApi = mainApi,
//                            navHostController = navController,
//                            viewModel = TODO()
//                        )
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