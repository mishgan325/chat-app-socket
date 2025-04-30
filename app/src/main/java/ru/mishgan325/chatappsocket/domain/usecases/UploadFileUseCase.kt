package ru.mishgan325.chatappsocket.domain.usecases

import okhttp3.MultipartBody
import ru.mishgan325.chatappsocket.data.ApiRepository
import ru.mishgan325.chatappsocket.utils.NetworkResult
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val apiRepository: ApiRepository
) {
    suspend operator fun invoke(filePart: MultipartBody.Part) = apiRepository.uploadFile(filePart)
}
