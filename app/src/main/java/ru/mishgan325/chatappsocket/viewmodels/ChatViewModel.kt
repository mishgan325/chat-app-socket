package ru.mishgan325.chatappsocket.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.data.mappers.toMessage
import ru.mishgan325.chatappsocket.domain.models.Message
import ru.mishgan325.chatappsocket.domain.models.User
import ru.mishgan325.chatappsocket.domain.usecases.AddUserToChatUseCase
import ru.mishgan325.chatappsocket.domain.usecases.DeleteMessageUseCase
import ru.mishgan325.chatappsocket.domain.usecases.DisconnectWebSocketUseCase
import ru.mishgan325.chatappsocket.domain.usecases.EditMessageUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetChatMessagesUseCase
import ru.mishgan325.chatappsocket.domain.usecases.GetFileLinkUseCase
import ru.mishgan325.chatappsocket.domain.usecases.ObserveIncomingMessagesUseCase
import ru.mishgan325.chatappsocket.domain.usecases.SearchUsersUseCase
import ru.mishgan325.chatappsocket.domain.usecases.SendMessageUseCase
import ru.mishgan325.chatappsocket.domain.usecases.SubscribeWebSocketUseCase
import ru.mishgan325.chatappsocket.domain.usecases.UnsubscribeWebSocketUseCase
import ru.mishgan325.chatappsocket.domain.usecases.UploadFileUseCase
import ru.mishgan325.chatappsocket.utils.NetworkResult
import ru.mishgan325.chatappsocket.utils.NetworkResult.Error
import ru.mishgan325.chatappsocket.utils.SessionManager
import ru.mishgan325.chatappsocket.utils.getFilePartFromUri
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val subscribeWebSocketUseCase: SubscribeWebSocketUseCase,
    private val observeIncomingMessagesUseCase: ObserveIncomingMessagesUseCase,
    private val disconnectWebSocketUseCase: DisconnectWebSocketUseCase,
    private val unsubscribeWebSocketUseCase: UnsubscribeWebSocketUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val addUserToChatUseCase: AddUserToChatUseCase,
    val sessionManager: SessionManager
) : ViewModel() {

    private val TAG = "ChatViewModel"

    private val _chatMessages = MutableStateFlow<PagingData<Message>>(PagingData.empty())
    val chatMessages: StateFlow<PagingData<Message>> = _chatMessages

    private val _newMessages = MutableStateFlow<List<Message>>(emptyList())
    val newMessages: StateFlow<List<Message>> = _newMessages

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults

    private val _attachedFileName = MutableStateFlow("")
    val attachedFileName: StateFlow<String> = _attachedFileName

    private val _uploadingFileState = MutableStateFlow<NetworkResult<Unit>>(NetworkResult.Idle())
    val uploadingFileState: StateFlow<NetworkResult<Unit>> = _uploadingFileState


    private var incomingMessagesJob: Job? = null  // Для отслеживания подписки

    // Подписка на WebSocket и обработка входящих сообщений
    fun subscribeToWebSocket(chatId: Long) {
        // Подписываемся на WebSocket
        subscribeWebSocketUseCase.invoke(chatId)

        // Если уже есть активная подписка, отменяем её
        incomingMessagesJob?.cancel()

        // Запускаем новый наблюдатель для входящих сообщений
        incomingMessagesJob = viewModelScope.launch {
            observeIncomingMessagesUseCase.invoke().collect { messageWs ->
                Log.d(TAG, "Получено новое сообщение: $messageWs")
                val newMessage = messageWs.toMessage(sessionManager.getUsername().toString())
                _newMessages.value = listOf(newMessage) + _newMessages.value
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
                is NetworkResult.Idle -> Log.d(TAG, "Nothing happening")
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
        _chatMessages.value = _chatMessages.value.map { message ->
            if (message.id == chatMessageId) {
                message.copy(content = newContent)
            } else {
                message
            }
        }

        _newMessages.value = _newMessages.value.map { message ->
            if (message.id == chatMessageId) {
                message.copy(content = newContent)
            } else {
                message
            }
        }
    }

    fun deleteMessage(chatMessageId: Long) {
        viewModelScope.launch {
            when (val result = deleteMessageUseCase.invoke(chatMessageId)) {
                is NetworkResult.Idle -> Log.d(TAG, "Idle")
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
        _chatMessages.value = _chatMessages.value.filter { it.id != chatMessageId }
        _newMessages.value = _newMessages.value.filter { it.id != chatMessageId }
    }

    fun sendMessage(content: String, chatId: Long) {
        Log.d(TAG, "Попытка отправить сообщение: $content")
        viewModelScope.launch {
            sendMessageUseCase.invoke(content, _attachedFileName.value, chatId)
            clearAttachedFile()
        }
    }

    fun unsubscribe(chatId: Long) {
        incomingMessagesJob?.cancel()  // Останавливаем наблюдение
        _newMessages.value = emptyList()  // Очищаем список новых сообщений
        unsubscribeWebSocketUseCase.invoke(chatId)
    }

    fun addUser(chatId: Long, userId: Long) {
        viewModelScope.launch {
            Log.d(TAG, "Добавление пользователя $userId в чат $chatId")
            viewModelScope.launch {
                addUserToChatUseCase.invoke(chatId, userId)
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
                is NetworkResult.Idle -> Log.d(TAG, "Idle")
                is NetworkResult.Error -> {
                    Log.d(TAG, "Search error: ${result.message}")
                    _searchResults.value = emptyList()
                }

                is NetworkResult.Loading -> {
                    // optionally show loading
                }

                is NetworkResult.Success -> {
                    _searchResults.value = result.data?.map { dto ->
                        User(dto.id, dto.username)
                    } ?: emptyList()
                }
            }
        }
    }

    fun uploadFile(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uploadingFileState.value = NetworkResult.Loading()

            val filePart = getFilePartFromUri(uri, context)
            if (filePart != null) {
                when (val result = uploadFileUseCase(filePart)) {
                    is NetworkResult.Idle -> Log.d(TAG, "Idle")
                    is NetworkResult.Error -> {
                        Log.e(TAG, "Ошибка загрузки файла: ${result.message}")
                        _uploadingFileState.value = NetworkResult.Error(Unit, "Can't upload file")
                    }
                    is NetworkResult.Loading -> {
                        Log.d(TAG, "Loading")
                        _uploadingFileState.value = NetworkResult.Loading()
                    }
                    is NetworkResult.Success -> {
                        val fileName = result.data?.filePath ?: ""
                        _attachedFileName.value = fileName
                        Log.d(TAG, "Файл загружен: $fileName")
                        _uploadingFileState.value = NetworkResult.Success(Unit)
                    }
                }
            } else {
                Log.e(TAG, "Не удалось получить файл из Uri")
            }
        }
    }



    fun clearAttachedFile() {
        _attachedFileName.value = ""
    }

}