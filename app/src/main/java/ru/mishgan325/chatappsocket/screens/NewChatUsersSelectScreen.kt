package ru.mishgan325.chatappsocket.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import ru.mishgan325.chatappsocket.screens.components.SelectableUserListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectUserScreen(
    users: List<User>,
    onCreateClicked: (String, List<User>) -> Unit
) {
    var chatName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
//                    onCreateClicked(chatName, users.filter { it.isSelected })
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = "Create")
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f).padding(bottom = 70.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(users) { user ->
                        SelectableUserListItem(
                            userName = user.username,
                            isChecked = true,
                            onCheckedChange = {},
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}


//@Composable
//@Preview
//fun SelectUserScreen() {
//    MaterialTheme {
//        SelectUserScreen(
//            users = listOf(
//                User(1, "User 1"),
//                User(2, "User 2"),
//                User(3, "User 3")
//            ),
//            onCreateClicked = { _, _ -> }
//        )
//    }
//}