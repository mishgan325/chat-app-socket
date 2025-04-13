package ru.mishgan325.chatappsocket.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mishgan325.chatappsocket.presentation.components.ChatListItem
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.models.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    chats: List<Chat> = emptyList(),
    onAddChat: () -> Unit = {},
    onChatClick: (Chat) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chats)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddChat,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_new_chat))
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                items(chats) { chat ->
                    ChatListItem(
                        chat = chat,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { onChatClick(chat) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ChatListPreview() {
    ChatListScreen(
        listOf
            (
            Chat(
                1,
                "ОЧЕНЬ ДЛИННОЕ НАЗВАНИЕ ПРОСТО АХЕРЕТЬ КАКАЯ ДЛИННААЯ",
                "Крутая группа"
            ),
            Chat(
                2,
                "фвыажлдофывлаждфыовлда",
                "Приватная беседа"
            ),
            Chat(
                3,
                "ОЧЕНЬ фывафыва НАЗВАНИЕ ПРОСТО АХЕРЕТЬ КАКАЯ ДЛИННААЯ",
                "Крутая фвыафыва"
            )
        )
    )
}