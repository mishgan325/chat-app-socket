package ru.mishgan325.chatappsocket.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.mishgan325.chatappsocket.models.User
import ru.mishgan325.chatappsocket.screens.components.SimpleUserListItem

@Composable
fun CreateChatScreen(
    users: List<User>
) {
    var chatName by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Create")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Поле ввода названия чата
            OutlinedTextField(
                value = chatName,
                onValueChange = { chatName = it },
                label = { Text("Название чата") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )

            // Список пользователей с ограничением высоты
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 70.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(users) { user ->
                        SimpleUserListItem(
                            userName = user.username,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun CreateChatScreenPreview() {
    MaterialTheme {
        CreateChatScreen(
            users = listOf(
                User(1, "User 1"),
                User(2, "User 2"),
                User(3, "User 3"),
                User(2, "User 4"),
                User(2, "User 5"),
                User(2, "User 6"),
                User(2, "User 7"),
                User(2, "User 8"),
                User(2, "User 9"),
                User(2, "User 10"),
                User(2, "User 11"),
                User(2, "User 12"),
                User(2, "User 13"),
                User(2, "User 2"),
                User(2, "User 2"),
            )
        )
    }
}