package ru.mishgan325.chatappsocket.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import ru.mishgan325.chatappsocket.data.ApiRepository
import ru.mishgan325.chatappsocket.data.paging.ChatMessagesPagingSource
import ru.mishgan325.chatappsocket.models.Message
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    fun getChatMessages(chatRoomId: Long): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                initialLoadSize = 3
            ),
            pagingSourceFactory = {
                ChatMessagesPagingSource(apiRepository, chatRoomId, sessionManager.getUserId())
            }
        ).flow.cachedIn(viewModelScope) // Кешируем результаты для сохранения в памяти
    }
}
