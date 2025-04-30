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
import ru.mishgan325.chatappsocket.domain.models.User
import ru.mishgan325.chatappsocket.domain.usecases.CreateGroupChatUseCase
import ru.mishgan325.chatappsocket.domain.usecases.CreatePrivateChatUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetUsersUseCase
import ru.mishgan325.chatappsocket.domain.usecases.SearchUsersUseCase
import ru.mishgan325.chatappsocket.dto.UserDto
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

    private val _getUsersResult = MutableStateFlow<NetworkResult<List<UserDto>>>(NetworkResult.Loading())
    val getUsersResult: StateFlow<NetworkResult<List<UserDto>>> = _getUsersResult.asStateFlow()

    private val _getUsersState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Loading())
    val getUsersState: StateFlow<NetworkResult<Unit>> = _getUsersState.asStateFlow()

    private val _createChatResult = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle())
    val createChatResult: StateFlow<NetworkResult<Unit>> = _createChatResult.asStateFlow()

    private val _createChatState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Loading())
    val createChatState: StateFlow<NetworkResult<Unit>> = _createChatState.asStateFlow()

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

            val result = getUsersUseCase.invoke()
            _getUsersResult.value = result

            when (result) {
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error: ${result.message}")
                    _getUsersState.value = NetworkResult.Error(null, result.message)
                }
                is NetworkResult.Loading -> {
                    _getUsersState.value = NetworkResult.Loading()
                }
                is NetworkResult.Success -> {
                    Log.d(TAG, "SUCCESS: ${result.data}")
                    _getUsersState.value = NetworkResult.Success(Unit)
                }
                else -> Unit
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

    private fun createPrivateChat(name: String, userId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _createChatState.value = NetworkResult.Loading()

            val result = createPrivateChatUseCase.invoke(name, userId)
            _createChatResult.value = result

            when (result) {
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error: ${result.message}")
                    _createChatState.value = NetworkResult.Error(null, result.message)
                }
                is NetworkResult.Loading -> {
                    _createChatState.value = NetworkResult.Loading()
                }
                is NetworkResult.Success -> {
                    _createChatState.value = NetworkResult.Success(Unit)
                    onSuccess()
                }
                else -> Unit
            }
        }
    }

    private fun createGroupChat(name: String, memberIds: List<Long>, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _createChatState.value = NetworkResult.Loading()

            val result = createGroupChatUseCase.invoke(name, memberIds)
            _createChatResult.value = result

            when (result) {
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error: ${result.message}")
                    _createChatState.value = NetworkResult.Error(null, result.message)
                }
                is NetworkResult.Loading -> {
                    _createChatState.value = NetworkResult.Loading()
                }
                is NetworkResult.Success -> {
                    _createChatState.value = NetworkResult.Success(Unit)
                    onSuccess()
                }
                else -> Unit
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
                    _searchResults.value = result.data?.map { User(it.id, it.username) } ?: emptyList()
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Search error: ${result.message}")
                    _searchResults.value = emptyList()
                }
                else -> Unit
            }
        }
    }
}
