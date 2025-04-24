package ru.mishgan325.chatappsocket.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.data.mappers.toMessage
import ru.mishgan325.chatappsocket.data.websocket.model.ChatMessageWs
import ru.mishgan325.chatappsocket.domain.usecases.DeleteMessageUseCase
import ru.mishgan325.chatappsocket.domain.usecases.EditMessageUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetChatMessagesUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetFileLinkUseCase
import ru.mishgan325.chatappsocket.domain.models.Message
import ru.mishgan325.chatappsocket.domain.usecases.ConnectToWebSocketUseCase
import ru.mishgan325.chatappsocket.domain.usecases.DisconnectWebSocketUseCase
import ru.mishgan325.chatappsocket.domain.usecases.ObserveIncomingMessagesUseCase
import ru.mishgan325.chatappsocket.domain.usecases.SendMessageUseCase
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val getFileLinkUseCase: GetFileLinkUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val connectToWebSocketUseCase: ConnectToWebSocketUseCase,
    private val observeIncomingMessagesUseCase: ObserveIncomingMessagesUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val TAG = "ChatViewModel"

    private val _chatMessages = MutableStateFlow<PagingData<Message>>(PagingData.empty())
    val chatMessages: StateFlow<PagingData<Message>> = _chatMessages

    private val _newMessages = MutableStateFlow<List<Message>>(emptyList())
    val newMessages: StateFlow<List<Message>> = _newMessages

    fun connectToWebSocket(chatId: Long) {
        connectToWebSocketUseCase.invoke(sessionManager.getAuthToken().toString(), chatId)
        observeIncomingMessages()
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            observeIncomingMessagesUseCase.invoke().collect { message ->
                Log.d(TAG, "Получено новое сообщение: $message")
            }
        }
    }


    fun loadChatMessages(chatRoomId: Long) {
        viewModelScope.launch {
            getChatMessagesUseCase(chatRoomId)
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _chatMessages.value = pagingData
                }
        }
    }

    fun editMessage(chatMessageId: Long, newContent: String) {
        viewModelScope.launch {
            when (val result = editMessageUseCase.invoke(chatMessageId, newContent)) {
                is NetworkResult.Error -> Log.d(TAG, "Error: ${result.message}")
                is NetworkResult.Loading -> Log.d(TAG, "Auth is loading")
                is NetworkResult.Success -> {
                    Log.d(TAG, "SUCCESS: ")
                    updateMessageLocally(chatMessageId, newContent)
                }
            }
        }
    }

    fun updateMessageLocally(chatMessageId: Long, newContent: String) {
        val currentData = _chatMessages.value
        val newData = currentData.map { message ->
            if (message.id == chatMessageId) {
                message.copy(content = newContent)
            } else {
                message
            }
        }
        _chatMessages.value = newData
    }

    fun deleteMessage(chatMessageId: Long) {
        viewModelScope.launch {
            when (val result = deleteMessageUseCase.invoke(chatMessageId)) {
                is NetworkResult.Error -> Log.d(TAG, "Delete error: ${result.message}")
                is NetworkResult.Loading -> Log.d(TAG, "Deleting...")
                is NetworkResult.Success -> {
                    Log.d(TAG, "Message deleted")
                    deleteMessageLocally(chatMessageId)
                }
            }
        }
    }

    private fun deleteMessageLocally(chatMessageId: Long) {
        val currentData = _chatMessages.value
        val filteredData = currentData.filter { it.id != chatMessageId }
        _chatMessages.value = filteredData
    }

    // Используем SendMessageUseCase для отправки сообщений
    fun sendMessage(content: String, fileUrl: String = "", chatId: Long) {
        viewModelScope.launch {
            sendMessageUseCase.invoke(content, fileUrl, chatId)
        }
    }

    fun disconnectWebSocket() {
        disconnectWebSocketUseCase.invoke()
    }
}