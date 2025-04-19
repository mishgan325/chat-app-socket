package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class EditMessageUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(chatMessageId: Long, newMessage: String) = apiRepository.editMessage(chatMessageId, newMessage)
}