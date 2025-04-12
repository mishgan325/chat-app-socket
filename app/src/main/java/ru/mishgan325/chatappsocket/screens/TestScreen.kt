package ru.mishgan325.chatappsocket.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import ru.mishgan325.chatappsocket.api.ApiInterface
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.screens.components.ApiRequestTester

@Composable
@Preview
fun TestScreen() {
    val context = LocalContext.current
    val apiInterface = remember { ApiConfig.retrofit.create(ApiInterface::class.java) }

    Column {
        ApiRequestTester(apiInterface)

        // Другие компоненты...
    }
}