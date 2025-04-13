//package ru.mishgan325.chatappsocket.presentation.components
//
//import android.util.Log
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.launch
//import ru.mishgan325.chatappsocket.api.MainApi
//
//@Composable
//fun ApiRequestTester(mainApi: MainApi) {
//    val coroutineScope = rememberCoroutineScope()
//    val tag = "API_TEST"
//
//    Button(modifier = Modifier.padding(top = 100.dp), onClick = {
//        Log.d("API_REQUEST_TESTER", "CLICK")
//        coroutineScope.launch {
//            try {
//                // Пример выполнения запроса на получение чатов
//                val response = mainApi.getMyChatRooms()
//                Log.d(tag, "Response code: ${response.code()}")
//
//                if (response.isSuccessful) {
//                    response.body()?.let { chats ->
//                        Log.d(tag, "Received ${chats.size} chats:")
//                        chats.forEach { chat ->
//                            Log.d(tag, "Chat: ${chat.name} (ID: ${chat.id})")
//                        }
//                    }
//                } else {
//                    Log.e(tag, "Error: ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e(tag, "Exception: ${e.localizedMessage}")
//            }
//        }
//    }) {
//        Text("Execute Request")
//    }
//}