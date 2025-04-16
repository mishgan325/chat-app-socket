package ru.mishgan325.chatappsocket.domain.usecases

import ru.mishgan325.chatappsocket.data.ApiRepository
import javax.inject.Inject

class GetFileLinkUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend operator fun invoke(fileUrl: String) = apiRepository.getFileLink(fileUrl)
}