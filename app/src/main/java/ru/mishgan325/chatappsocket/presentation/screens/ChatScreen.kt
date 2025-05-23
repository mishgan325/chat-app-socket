package ru.mishgan325.chatappsocket.presentation.screens

import ChatBubble
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import ru.mishgan325.chatappsocket.presentation.components.AddUserToChatDialog
import ru.mishgan325.chatappsocket.presentation.components.LoadingItem
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.viewmodels.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatRoomId: Long,
    chatName: String,
    isPrivate: Boolean,
    viewModel: ChatViewModel,
) {
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

    val context = LocalContext.current
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadFile(uri, context)
        }
    }

    val uploadingState by viewModel.uploadingFileState.collectAsState()

    val isUploading = uploadingState is NetworkResult.Loading

    if (isUploading) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
                    if (!isPrivate) {
                        IconButton(onClick = { showAddUserDialog = true }) {
                            Icon(
                                Icons.Default.PersonAdd,
                                contentDescription = "Добавить пользователя"
                            )
                        }
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
                IconButton(onClick = {
                    pickFileLauncher.launch("*/*")
                }) {
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
                            viewModel.sendMessage(currentInput, chatRoomId)
                            currentInput = ""
                        }
                    },
                    enabled = !isUploading
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