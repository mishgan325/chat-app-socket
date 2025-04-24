package ru.mishgan325.chatappsocket.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.mishgan325.chatappsocket.data.ApiRepository
import ru.mishgan325.chatappsocket.data.mappers.toChatMessageDto
import ru.mishgan325.chatappsocket.data.mappers.toMessage
import ru.mishgan325.chatappsocket.domain.usecases.GetFileLinkUseCase
import ru.mishgan325.chatappsocket.domain.models.Message
import ru.mishgan325.chatappsocket.utils.NetworkResult

class ChatMessagesPagingSource(
    private val apiRepository: ApiRepository,
    private val chatRoomId: Long,
    private val userId: Long?,
    private val getFileLinkUseCase: GetFileLinkUseCase
) : PagingSource<Int, Message>() {

    companion object {
        private const val TAG = "ChatMessagesPaging"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val page = params.key ?: 0

        return try {
            val response = apiRepository.getChatMessages(chatRoomId, page, params.loadSize, "timestamp,desc")

            if (response is NetworkResult.Success) {
                val data = response.data
                val messagesRaw = data?.content?.map { it.toChatMessageDto().toMessage(userId) } ?: emptyList()

                val messages = messagesRaw.map { message ->
                    if (!message.fileUrl.isNullOrEmpty()) {
                        val result = getFileLinkUseCase(message.fileUrl)
                        if (result is NetworkResult.Success && result.data != null) {
                            Log.d(TAG, "fileUrl заменён: ${message.fileUrl} -> ${result.data}")
                            message.copy(fileUrl = result.data.url)
                        } else {
                            Log.d(TAG, "Ошибка получения fileUrl: ${message.fileUrl}")
                            message
                        }
                    } else {
                        message
                    }
                }

                val nextKey = if ((data?.pageable?.pageNumber ?: 0) < (data?.totalPages?.minus(1) ?: 0)) {
                    data?.pageable?.pageNumber?.plus(1)
                } else {
                    null
                }

                LoadResult.Page(
                    data = messages,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(Exception("Error loading data"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки сообщений: ${e.message}", e)
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition
    }
}