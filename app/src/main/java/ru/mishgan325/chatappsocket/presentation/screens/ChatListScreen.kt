package ru.mishgan325.chatappsocket.presentation.screens

import android.app.Activity
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import ru.mishgan325.chatappsocket.presentation.components.ChatListItem
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.models.Chat
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

    // Обработка кнопки "Назад" для выхода из приложения
    BackHandler {
        // Завершаем приложение при нажатии назад
        (context as Activity).finish()
    }


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                chatListViewModel.getMyChats()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state = chatListViewModel.chatListResponse.observeAsState().value ?: NetworkResult.Loading()

    val chats: List<Chat> = when (state) {
        is NetworkResult.Success -> state.data?.map { it.toChat() } ?: emptyList()
        is NetworkResult.Error -> {
            navHostController.navigate(Screen.Login.route)
            emptyList()
        }
        is NetworkResult.Loading -> emptyList()
    }

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
                onClick = {
                    navHostController.navigate(Screen.CreateNewChat.route)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        items(chats) { chat ->
                            ChatListItem(
                                chat = chat,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        navHostController.navigate(
                                            Screen.Chat.withArgs(chat.id.toString(), chat.name)
                                        )
                                    }
                            )
                        }
                    }
                }

                is NetworkResult.Error -> {
                    // На случай, если вдруг переход на логин не произошёл
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


//@Composable
//@Preview
//fun ChatListPreview() {
//    ChatListScreen(
//        listOf
//            (
//            Chat(
//                1,
//                "ОЧЕНЬ ДЛИННОЕ НАЗВАНИЕ ПРОСТО АХЕРЕТЬ КАКАЯ ДЛИННААЯ",
//                "Крутая группа"
//            ),
//            Chat(
//                2,
//                "фвыажлдофывлаждфыовлда",
//                "Приватная беседа"
//            ),
//            Chat(
//                3,
//                "ОЧЕНЬ фывафыва НАЗВАНИЕ ПРОСТО АХЕРЕТЬ КАКАЯ ДЛИННААЯ",
//                "Крутая фвыафыва"
//            )
//        )
//    )
//}