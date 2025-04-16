package ru.mishgan325.chatappsocket.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.domain.usecases.GetChatMessagesUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetFileLinkUseCase
import ru.mishgan325.chatappsocket.models.Message
import ru.mishgan325.chatappsocket.utils.NetworkResult
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val getFileLinkUseCase: GetFileLinkUseCase
) : ViewModel() {

    private val TAG = "ChatViewModel"

    fun getChatMessages(chatRoomId: Long): Flow<PagingData<Message>> {
        return getChatMessagesUseCase(chatRoomId).cachedIn(viewModelScope)
    }

    fun getFileLink(fileUrl: String) {
        viewModelScope.launch {
            when (val result = getFileLinkUseCase(fileUrl)) {
                is NetworkResult.Error -> Log.d(TAG, "Error: ${result.message}")
                is NetworkResult.Loading -> Log.d(TAG, "Auth is loading")
                is NetworkResult.Success -> Log.d(TAG, "SUCCESS: ${result.data}")
            }
        }
    }
}

