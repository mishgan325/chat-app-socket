package ru.mishgan325.chatappsocket.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.domain.usecases.DeleteMessageUseCase
import ru.mishgan325.chatappsocket.domain.usecases.EditMessageUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetChatMessagesUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetFileLinkUseCase
import ru.mishgan325.chatappsocket.models.Message
import ru.mishgan325.chatappsocket.utils.NetworkResult
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val getFileLinkUseCase: GetFileLinkUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase
) : ViewModel() {

    private val TAG = "ChatViewModel"

    private val _chatMessages = MutableStateFlow<PagingData<Message>>(PagingData.empty())
    val chatMessages: StateFlow<PagingData<Message>> = _chatMessages

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
            when( val result =  editMessageUseCase.invoke(chatMessageId, newContent)) {
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
}

