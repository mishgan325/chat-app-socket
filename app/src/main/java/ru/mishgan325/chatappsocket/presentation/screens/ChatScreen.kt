package ru.mishgan325.chatappsocket.presentation.screens

import ChatBubble
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import ru.mishgan325.chatappsocket.domain.models.User
import ru.mishgan325.chatappsocket.presentation.components.AddUserToChatDialog
import ru.mishgan325.chatappsocket.presentation.components.LoadingItem
import ru.mishgan325.chatappsocket.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatRoomId: Long,
    chatName: String,
    viewModel: ChatViewModel,
) {
    // загружаем сообщения один раз
    LaunchedEffect(chatRoomId) {
        viewModel.loadChatMessages(chatRoomId)
        viewModel.subscribeToWebSocket(chatRoomId)
    }

    DisposableEffect(chatRoomId) {
        onDispose {
            viewModel.unsubscribe(chatRoomId)
        }
    }

    val chatMessages = viewModel.chatMessages.collectAsLazyPagingItems()
    val newMessages = viewModel.newMessages.collectAsState()

    var currentInput by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }

    if (showAddUserDialog) {
        AddUserToChatDialog(
            onDismiss = { showAddUserDialog = false },
            onUserClick = {
                viewModel.addUser(chatRoomId, it)
                showAddUserDialog = false
            },
            viewModel = viewModel
        )
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chatName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showAddUserDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Добавить пользователя")
                    }
                }
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
                            // TODO: отправка сообщения
                            viewModel.sendMessage(currentInput, "", chatRoomId) //TODO: send files
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
            reverseLayout = true
        ) {
            items(newMessages.value.size) { index ->
                val messageList = newMessages.value
                val message = if (index < messageList.size) messageList[index] else null
                message?.let {
                    ChatBubble(
                        message = it,
                        onEditMessage = { messageId, newContent ->
                            viewModel.editMessage(messageId, newContent)
                        },
                        onDeleteMessage = { messageId ->
                            viewModel.deleteMessage(messageId)
                        }
                    )
                }
            }


            items(chatMessages.itemCount) { index ->
                val message = if (index < chatMessages.itemCount) chatMessages[index] else null
                message?.let {
                    ChatBubble(
                        message = it,
                        onEditMessage = { messageId, newContent ->
                            viewModel.editMessage(messageId, newContent)
                        },
                        onDeleteMessage = { messageId ->
                            viewModel.deleteMessage(messageId)
                        }
                    )
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