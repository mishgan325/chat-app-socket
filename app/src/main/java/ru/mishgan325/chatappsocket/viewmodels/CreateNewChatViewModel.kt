package ru.mishgan325.chatappsocket.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.domain.usecases.CreateGroupChatUseCase
import ru.mishgan325.chatappsocket.domain.usecases.CreatePrivateChatUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetUsersUseCase
import ru.mishgan325.chatappsocket.domain.usecases.SearchUsersUseCase
import ru.mishgan325.chatappsocket.dto.UserDto
import ru.mishgan325.chatappsocket.domain.models.User
import ru.mishgan325.chatappsocket.utils.NetworkResult
import javax.inject.Inject

@HiltViewModel
class CreateNewChatViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val createPrivateChatUseCase: CreatePrivateChatUseCase,
    private val createGroupChatUseCase: CreateGroupChatUseCase
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _chatName = MutableStateFlow("")
    val chatName: StateFlow<String> = _chatName.asStateFlow()

    private val _selectedUserIds = MutableStateFlow<List<Long>>(emptyList())
    val selectedUserIds: StateFlow<List<Long>> = _selectedUserIds.asStateFlow()

    private val _getUsersResponse = MutableLiveData<NetworkResult<List<UserDto>>>()
    val getUsersResponse: LiveData<NetworkResult<List<UserDto>>> get() = _getUsersResponse

    private val _getUsersState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Loading())
    val getUsersState: StateFlow<NetworkResult<Unit>> = _getUsersState

    private val _createChatResponse = MutableLiveData<NetworkResult<Unit>>()
    val createChatResponse: LiveData<NetworkResult<Unit>> = _createChatResponse

    private val _createChatState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Loading())
    val createChatState: StateFlow<NetworkResult<Unit>> = _createChatState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults.asStateFlow()


    private val TAG = "CreateNewChatViewModel"

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _getUsersState.value = NetworkResult.Loading()

            getUsersUseCase.invoke().let { result ->
                _getUsersResponse.value = result

                when (result) {
                    is NetworkResult.Error -> {
                        Log.d(TAG, "Error: ${result.message}")
                        _getUsersState.value = NetworkResult.Error(null, result.message)
                    }

                    is NetworkResult.Loading -> {
                        Log.d(TAG, "Register is loading")
                        _getUsersState.value = NetworkResult.Loading()
                    }

                    is NetworkResult.Success -> {
                        Log.d(TAG, "SUCCESS: ${result.data?.toString()}")
                        _getUsersState.value = NetworkResult.Success(Unit)
                    }
                }
            }
        }
    }

    fun onChatNameChange(newName: String) {
        _chatName.value = newName
    }

    fun onUserCheckedChange(userId: Long, isChecked: Boolean) {
        _selectedUserIds.value = if (isChecked) {
            _selectedUserIds.value + userId
        } else {
            _selectedUserIds.value - userId
        }
    }

    fun createChat(onInvalidSelection: (message: String) -> Unit, onSuccess: () -> Unit) {
        val selected = _selectedUserIds.value

        Log.d(TAG, "Selected users: $selected")
        Log.d(TAG, "Chat name: ${chatName.value}")

        if (selected.isEmpty()) {
            onInvalidSelection("Необходимо выбрать пользователей")
            return
        }

        val name = _chatName.value.trim()
        if (name.isEmpty()) {
            onInvalidSelection("Название не может быть пустым")
            return
        }

        if (selected.size == 1) {
            createPrivateChat(name, selected[0], onSuccess)
        } else {
            createGroupChat(name, selected, onSuccess)
        }
    }

    fun createPrivateChat(name: String, userId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _createChatState.value = NetworkResult.Loading()

            createPrivateChatUseCase.invoke(name, userId).let { result ->
                _createChatResponse.value = result

                when (result) {
                    is NetworkResult.Error -> {
                        Log.d(TAG, "Error: ${result.message}")
                        _createChatState.value = NetworkResult.Error(null, result.message)
                    }

                    is NetworkResult.Loading -> {
                        Log.d(TAG, "Register is loading")
                        _createChatState.value = NetworkResult.Loading()
                    }

                    is NetworkResult.Success -> {
                        Log.d(TAG, "SUCCESS: ${result.data?.toString()}")
                        _createChatState.value = NetworkResult.Success(Unit)
                        onSuccess()
                    }
                }
            }
        }
    }

    fun createGroupChat(name: String, memberIds: List<Long>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _createChatState.value = NetworkResult.Loading()

            createGroupChatUseCase.invoke(name, memberIds).let { result ->
                _createChatResponse.value = result

                when (result) {
                    is NetworkResult.Error -> {
                        Log.d(TAG, "Error: ${result.message}")
                        _createChatState.value = NetworkResult.Error(null, result.message)
                    }

                    is NetworkResult.Loading -> {
                        Log.d(TAG, "Register is loading")
                        _createChatState.value = NetworkResult.Loading()
                    }

                    is NetworkResult.Success -> {
                        Log.d(TAG, "SUCCESS: ${result.data?.toString()}")
                        _createChatState.value = NetworkResult.Success(Unit)
                        onSuccess()
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchUsers(query)
    }

    private fun searchUsers(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            val result = searchUsersUseCase.invoke(query)
            when (result) {
                is NetworkResult.Success -> {
                    _searchResults.value = result.data?.map { dto ->
                        User(dto.id, dto.username)
                    } ?: emptyList()
                }

                is NetworkResult.Error -> {
                    Log.d(TAG, "Search error: ${result.message}")
                    _searchResults.value = emptyList()
                }

                is NetworkResult.Loading -> {
                    // optionally show loading
                }
            }
        }
    }

}
