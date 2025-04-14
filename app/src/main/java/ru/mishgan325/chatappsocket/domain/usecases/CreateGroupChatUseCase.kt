package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class CreateGroupChatUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(name: String, memberIds: List<Long>) =
        apiRepository.createGroupChat(name, memberIds)
}