package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(chatMessageId: Long) = apiRepository.deleteMessage(chatMessageId)
}