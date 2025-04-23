package ru.mishgan325.chatappsocket.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.mishgan325.chatappsocket.data.ApiRepository
import ru.mishgan325.chatappsocket.data.paging.ChatMessagesPagingSource
import ru.mishgan325.chatappsocket.domain.models.Message
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val apiRepository: ApiRepository,
    private val sessionManager: SessionManager,
    private val getFileLinkUseCase: GetFileLinkUseCase
) {
    operator fun invoke(chatRoomId: Long): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3,
                enablePlaceholders = false,
                initialLoadSize = 3
            ),
            pagingSourceFactory = {
                ChatMessagesPagingSource(
                    apiRepository = apiRepository,
                    chatRoomId = chatRoomId,
                    userId = sessionManager.getUserId(),
                    getFileLinkUseCase = getFileLinkUseCase
                )
            }
        ).flow
    }
}
