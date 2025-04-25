package ru.mishgan325.chatappsocket.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mishgan325.chatappsocket.viewmodels.ChatViewModel

@Composable
fun AddUserToChatDialog(
    onDismiss: () -> Unit,
    onUserClick: (Long) -> Unit,
    viewModel: ChatViewModel
) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResults by viewModel.searchResults.collectAsState()

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Добавить пользователя") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newQuery ->
                        searchQuery = newQuery
                        viewModel.onSearchQueryChanged(newQuery)
                    },
                    label = { Text("Поиск") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(searchResults.size) { index ->
                        val user = searchResults[index]
                        AssistChip(
                            onClick = { onUserClick(user.id) },
                            label = { Text(user.username) },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    )
}



