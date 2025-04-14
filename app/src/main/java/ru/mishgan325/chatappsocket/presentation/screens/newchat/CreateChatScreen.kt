package ru.mishgan325.chatappsocket.presentation.screens.newchat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import ru.mishgan325.chatappsocket.presentation.components.SelectableUserListItem

@Composable
fun CreateChatScreen(
    users: List<User>
) {
    var chatName by remember { mutableStateOf("") }
    var selectedUserIds by remember { mutableStateOf(setOf<Long>()) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: handle chat creation using chatName and selectedUserIds
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Create Chat")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = chatName,
                onValueChange = { chatName = it },
                label = { Text("Название чата") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    val isSelected = selectedUserIds.contains(user.id)

                    SelectableUserListItem(
                        userName = user.username,
                        isSelected = isSelected,
                        onCheckedChange = { isChecked ->
                            selectedUserIds = if (isChecked) {
                                selectedUserIds + user.id
                            } else {
                                selectedUserIds - user.id
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateChatScreenPreview() {
    MaterialTheme {
        CreateChatScreen(
            users = List(12) { index -> User(index.toLong(), "User ${index + 1}") }
        )
    }
}
