package ru.mishgan325.chatappsocket.presentation.screens

import ChatBubble
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import ru.mishgan325.chatappsocket.data.api.model.ChatMessagesResponse
import ru.mishgan325.chatappsocket.presentation.components.LoadingItem
import ru.mishgan325.chatappsocket.viewmodels.ChatViewModel

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatRoomId: Long,
    chatName: String,
    viewModel: ChatViewModel,
) {
    val chatMessages = viewModel.getChatMessages(chatRoomId).collectAsLazyPagingItems()
    var currentInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chatName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.AttachFile, contentDescription = "Attach file")
                }
                TextField(
                    value = currentInput,
                    onValueChange = { currentInput = it },
                    placeholder = { Text("Введите сообщение") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = {
                        if (currentInput.isNotBlank()) {
//                            onSendMessage(currentInput)

                            currentInput = ""
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 64.dp),
        ) {
            items(chatMessages.itemCount) { index ->
                chatMessages[index]?.let { message ->
                    ChatBubble(message = message)
                }
            }

            when (chatMessages.loadState.append) {
                is LoadState.Loading -> {
                    item { LoadingItem() }
                }

                is LoadState.Error -> {
                    item { Text("Ошибка при подгрузке") }
                }

                else -> Unit
            }

            when (chatMessages.loadState.refresh) {
                is LoadState.Loading -> {
                    item { LoadingItem() }
                }

                is LoadState.Error -> {
                    item { Text("Ошибка загрузки") }
                }

                else -> Unit
            }
        }
    }
}