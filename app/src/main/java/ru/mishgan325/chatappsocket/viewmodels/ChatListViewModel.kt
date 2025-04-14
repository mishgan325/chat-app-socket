package ru.mishgan325.chatappsocket.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.domain.usecases.GetMyChatRoomsUseCase
import ru.mishgan325.chatappsocket.dto.ChatRoomDto
import ru.mishgan325.chatappsocket.utils.NetworkResult
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getMyChatRoomsUseCase: GetMyChatRoomsUseCase
) : ViewModel() {

    private val _chatListResponse = MutableLiveData<NetworkResult<List<ChatRoomDto>>>()
    val chatListResponse: LiveData<NetworkResult<List<ChatRoomDto>>> get() = _chatListResponse

    private val _chatListState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Loading())
    val chatListState: StateFlow<NetworkResult<Unit>> = _chatListState

    private val TAG = "ChatListViewModel"

    fun getMyChats() {
        viewModelScope.launch {
            _chatListState.value = NetworkResult.Loading()

            getMyChatRoomsUseCase.invoke().let { result ->
                _chatListResponse.value = result

                when (result) {
                    is NetworkResult.Error -> {
                        Log.d(TAG, "Error: ${result.message}")
                        _chatListState.value = NetworkResult.Error(null, result.message)
                    }

                    is NetworkResult.Loading -> {
                        Log.d(TAG, "Register is loading")
                        _chatListState.value = NetworkResult.Loading()
                    }

                    is NetworkResult.Success -> {
                        Log.d(TAG, "SUCCESS: ${result.data?.toString()}")
                        _chatListState.value = NetworkResult.Success(Unit)
                    }
                }
            }
        }
    }
}