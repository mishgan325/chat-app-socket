package ru.mishgan325.chatappsocket.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.models.User
import ru.mishgan325.chatappsocket.presentation.components.ChatListItem
import ru.mishgan325.chatappsocket.presentation.components.SelectableUserListItem
import ru.mishgan325.chatappsocket.presentation.navigation.Screen
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.viewmodels.CreateNewChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewChatScreen(
    navHostController: NavHostController,
    createNewChatViewModel: CreateNewChatViewModel
) {
    val context = LocalContext.current

    val state =
        createNewChatViewModel.getUsersResponse.observeAsState().value ?: NetworkResult.Loading()

    val users: List<User> = when (state) {
        is NetworkResult.Success -> state.data?.map { it.toUser() } ?: emptyList()
        is NetworkResult.Error -> {
            navHostController.navigate(Screen.Chats.route)
            emptyList()
        }

        is NetworkResult.Loading -> emptyList()
    }

    val chatName by createNewChatViewModel.chatName.collectAsState()
    val selectedUserIds by createNewChatViewModel.selectedUserIds.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_new_chat)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    createNewChatViewModel.createChat(
                        onInvalidSelection = { message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                .show()// Покажи Toast или Snackbar: "Выберите минимум двух пользователей и введите название чата"
                        },
                        onSuccess = {
                            navHostController.navigate(Screen.Chats.route) {
                                popUpTo(Screen.Chats.route) { inclusive = true }
                            }
                        }
                    )
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
                onValueChange = createNewChatViewModel::onChatNameChange,
                label = { Text("Название чата") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )
            when (state) {
                is NetworkResult.Loading -> {
                    // Плавная анимация может быть добавлена через AnimatedVisibility, но тут просто красиво по центру:
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

                is NetworkResult.Success -> {
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
                                    createNewChatViewModel.onUserCheckedChange(user.id, isChecked)
                                }
                            )
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
