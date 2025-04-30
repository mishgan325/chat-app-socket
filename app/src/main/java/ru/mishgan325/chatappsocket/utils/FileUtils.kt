package ru.mishgan325.chatappsocket.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun getFilePartFromUri(uri: Uri, context: Context): MultipartBody.Part? {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return null
    val fileName = getFileName(uri, contentResolver) ?: "file"

    val tempFile = File(context.cacheDir, fileName)
    tempFile.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    val requestFile = tempFile.asRequestBody("application/octet-stream".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("file", fileName, requestFile)
}

fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri.path?.substringAfterLast('/')
    }
    return result
}
