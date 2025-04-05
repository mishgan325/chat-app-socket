package ru.mishgan325.chatappsocket.utils

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var sessionManager: SessionManager? = null
    private val excludedPaths = listOf("/auth/sign-in", "/auth/sign-up")

    fun initialize(context: Context) {
        sessionManager = SessionManager(context)
    }

    val retrofit: Retrofit by lazy {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val path = originalRequest.url.encodedPath

                // Не добавляем токен для auth-эндпоинтов
                if (path in excludedPaths) {
                    return@addInterceptor chain.proceed(originalRequest)
                }

                val token = sessionManager?.getAuthToken()
                val request = originalRequest.newBuilder()
                    .apply {
                        token?.let {
                            addHeader("Authorization", "Bearer $it")
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}