package ru.mishgan325.chatappsocket.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.mishgan325.chatappsocket.data.ApiRepository
import ru.mishgan325.chatappsocket.data.mappers.toChatMessageDto
import ru.mishgan325.chatappsocket.data.mappers.toMessage
import ru.mishgan325.chatappsocket.models.Message
import ru.mishgan325.chatappsocket.utils.NetworkResult

class ChatMessagesPagingSource(
    private val apiRepository: ApiRepository,
    private val chatRoomId: Long,
    private val userId: Long?
) : PagingSource<Int, Message>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        val page = params.key ?: 0 // начальная страница - 0
        return try {
            val response = apiRepository.getChatMessages(chatRoomId, page, params.loadSize, "timestamp,desc")
            if (response is NetworkResult.Success) {
                val data = response.data
                val messages = data?.content?.map { it.toChatMessageDto().toMessage(userId) } ?: emptyList()
                val nextKey = if ((data?.pageable?.pageNumber ?: 0) < (data?.totalPages?.minus(1) ?: 0)) {
                    data?.pageable?.pageNumber?.plus(1)
                } else {
                    null
                }
                LoadResult.Page(
                    data = messages,
                    prevKey = null,
                    nextKey = nextKey
                )
            } else {
                LoadResult.Error(Exception("Error loading data"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition
    }
}
