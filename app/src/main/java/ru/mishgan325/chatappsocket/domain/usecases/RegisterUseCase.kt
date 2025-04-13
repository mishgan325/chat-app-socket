package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend fun invoke(email: String, username: String, password: String) = apiRepository.register(email, username, password)
}