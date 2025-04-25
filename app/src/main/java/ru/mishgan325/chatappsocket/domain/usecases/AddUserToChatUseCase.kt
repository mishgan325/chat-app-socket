package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class AddUserToChatUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(chatRoomId: Long, userId: Long) =
        apiRepository.addUserToChat(chatRoomId, userId)
}