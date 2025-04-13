package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(username: String, password: String) = apiRepository.login(username, password)
}