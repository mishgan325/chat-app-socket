package ru.mishgan325.chatappsocket.presentation.screens

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.domain.models.Chat
import ru.mishgan325.chatappsocket.presentation.components.ChatListItem
import ru.mishgan325.chatappsocket.presentation.navigation.Screen
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.viewmodels.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navHostController: NavHostController,
    chatListViewModel: ChatListViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    BackHandler {
        (context as Activity).finish()
    }

    LaunchedEffect(Unit) {
        if (!chatListViewModel.isLoggedIn()) {
            Log.d("ChatListScreen", "Outdated token")
            navHostController.navigate(Screen.Login.route) {
                popUpTo(Screen.Chats.route) { inclusive = true }
            }
        } else {
            Log.d("ChatListScreen", "Fetching chats")
            chatListViewModel.getMyChats()
        }
    }


    // Используем collectAsState для получения состояния
    val state = chatListViewModel.chatListResponse.collectAsStateWithLifecycle()

    // Список чатов, отображаем его в зависимости от состояния
    val chats: List<Chat> = when (state.value) {
        is NetworkResult.Success -> state.value.data?.map { it.toChat() } ?: emptyList()
        is NetworkResult.Error -> {
            navHostController.navigate(Screen.Login.route)
            emptyList()
        }

        else -> emptyList()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.chats)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                navHostController.navigate(Screen.CreateNewChat.route)
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_new_chat))
        }
    }) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (state.value) {
                is NetworkResult.Error -> {
                    Text(
                        text = stringResource(R.string.error_loading_chats),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is NetworkResult.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        items(chats) { chat ->
                            ChatListItem(
                                chat = chat,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        navHostController.navigate(
                                            Screen.Chat.withArgs(
                                                chat.id, chat.name, chat.type == "PRIVATE"
                                            )
                                        )
                                    })
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = stringResource(R.string.loading),
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
