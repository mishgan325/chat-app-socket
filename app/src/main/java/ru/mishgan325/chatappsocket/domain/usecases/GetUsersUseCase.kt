package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke() = apiRepository.getUsers()
}