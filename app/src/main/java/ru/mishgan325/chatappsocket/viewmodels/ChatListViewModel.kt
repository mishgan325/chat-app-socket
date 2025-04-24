package ru.mishgan325.chatappsocket.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.data.websocket.WebSocketService
import ru.mishgan325.chatappsocket.domain.usecases.GetMyChatRoomsUseCase
import ru.mishgan325.chatappsocket.dto.ChatRoomDto
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getMyChatRoomsUseCase: GetMyChatRoomsUseCase,
    private val webSocketService: WebSocketService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _chatListResponse =
        MutableStateFlow<NetworkResult<List<ChatRoomDto>>>(NetworkResult.Loading())
    val chatListResponse: StateFlow<NetworkResult<List<ChatRoomDto>>> = _chatListResponse

    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> = _navigateToLogin

    private val TAG = "ChatListViewModel"

    fun getMyChats() {
        if (!sessionManager.isLoggedIn()) {
            Log.d(TAG, "Пользователь не авторизован, отмена")
            if (!_navigateToLogin.value) { // Проверяем, что переход ещё не был сделан
                _navigateToLogin.value = true // Триггер на навигацию
            }
            return
        }
        viewModelScope.launch {
            _chatListResponse.value = NetworkResult.Loading()

            getMyChatRoomsUseCase.invoke().let { result ->
                _chatListResponse.value = result

                when (result) {
                    is NetworkResult.Error -> {
                        Log.d(TAG, "Error: ${result.message}")
                        if (!_navigateToLogin.value) { // Проверяем, что переход ещё не был сделан
                            _navigateToLogin.value = true // Триггер на навигацию
                        }
                    }
                    is NetworkResult.Loading -> {
                        // Загрузка, ничего не меняем
                    }
                    is NetworkResult.Success -> {
                        Log.d(TAG, "SUCCESS: ${result.data?.toString()}")
                        webSocketService.connect()
                    }
                }
            }
        }
    }


    fun onNavigatedToLogin() {
        _navigateToLogin.value = false
    }
}
