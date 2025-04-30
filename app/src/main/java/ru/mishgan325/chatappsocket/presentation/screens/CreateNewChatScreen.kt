package ru.mishgan325.chatappsocket.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.domain.models.User
import ru.mishgan325.chatappsocket.presentation.components.SelectableUserListItem
import ru.mishgan325.chatappsocket.presentation.navigation.Screen
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.viewmodels.CreateNewChatViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateNewChatScreen(
    navHostController: NavHostController, createNewChatViewModel: CreateNewChatViewModel
) {
    val context = LocalContext.current

    val state = createNewChatViewModel.getUsersResult.collectAsStateWithLifecycle()

    LaunchedEffect(state.value) {
        if (state.value is NetworkResult.Error) {
            Toast.makeText(context, "Ошибка загрузки пользователей", Toast.LENGTH_SHORT).show()
            navHostController.navigate(Screen.Chats.route)
        }
    }

    val allUsers: List<User> = when (state.value) {
        is NetworkResult.Success -> state.value.data?.map { it.toUser() } ?: emptyList()
        is NetworkResult.Error -> {
            emptyList()
        }
        else -> emptyList()
    }

    val searchQuery by createNewChatViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by createNewChatViewModel.searchResults.collectAsStateWithLifecycle()
    val chatName by createNewChatViewModel.chatName.collectAsStateWithLifecycle()
    val selectedUserIds by createNewChatViewModel.selectedUserIds.collectAsStateWithLifecycle()

    val usersToDisplay = if (searchQuery.isNotBlank()) searchResults else allUsers

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.create_new_chat)) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                createNewChatViewModel.createChat(onInvalidSelection = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }, onSuccess = {
                    navHostController.navigate(Screen.Chats.route) {
                        popUpTo(Screen.Chats.route) { inclusive = true }
                    }
                })
            }) {
            Icon(Icons.Default.Check, contentDescription = "Create Chat")
        }
    }) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 💬 Поле ввода названия чата
            OutlinedTextField(
                value = chatName,
                onValueChange = createNewChatViewModel::onChatNameChange,
                label = { Text("Название чата") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 120.dp) // ограничиваем высоту
                    .verticalScroll(rememberScrollState()) // добавляем скролл при необходимости
                    .padding(bottom = 8.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedUserIds.forEach { userId ->
                        val user = allUsers.find { it.id == userId }
                        if (user != null) {
                            AssistChip(
                                onClick = {
                                    createNewChatViewModel.onUserCheckedChange(
                                        userId, false
                                    )
                                },
                                label = { Text(user.username) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove"
                                    )
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = AssistChipDefaults.assistChipColors(),
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = createNewChatViewModel::onSearchQueryChanged,
                label = { Text("Поиск пользователей") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            )

            when (state.value) {
                is NetworkResult.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 64.dp)
                    ) {
                        items(usersToDisplay) { user ->
                            val isSelected = selectedUserIds.contains(user.id)

                            SelectableUserListItem(
                                userName = user.username,
                                isSelected = isSelected,
                                onCheckedChange = { isChecked ->
                                    createNewChatViewModel.onUserCheckedChange(user.id, isChecked)
                                })
                        }
                    }
                }

                is NetworkResult.Error -> {
                    Text(
                        text = stringResource(R.string.error_loading_chats),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
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

//
//@Preview
//@Composable
//fun CreateChatScreenPreview() {
//    MaterialTheme {
//        CreateNewChatScreen(
//            users = List(12) { index -> User(index.toLong(), "User ${index + 1}") }
//        )
//    }
//}
