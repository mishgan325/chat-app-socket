package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class CreatePrivateChatUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(name: String, userId: Long) =
        apiRepository.createPrivateChat(name, userId)
}