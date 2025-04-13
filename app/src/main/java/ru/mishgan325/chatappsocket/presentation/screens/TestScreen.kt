//package ru.mishgan325.chatappsocket.presentation.screens
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import ru.mishgan325.chatappsocket.api.MainApi
//import ru.mishgan325.chatappsocket.utils.ApiConfig
//import ru.mishgan325.chatappsocket.presentation.components.ApiRequestTester
//
//@Composable
//@Preview
//fun TestScreen() {
//    val context = LocalContext.current
//    val mainApi = remember { ApiConfig.retrofit.create(MainApi::class.java) }
//
//    Column {
//        ApiRequestTester(mainApi)
//
//        // Другие компоненты...
//    }
//}