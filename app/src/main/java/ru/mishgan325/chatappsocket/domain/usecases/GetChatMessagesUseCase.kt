package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class GetChatMessagesUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(chatRoomId: Long, page: Int, size: Int, sort: String) = apiRepository.getChatMessages(
        chatRoomId,
        page,
        size,
        sort
    )
}