package ru.mishgan325.chatappsocket.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.mishgan325.chatappsocket.data.api.ApiService
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "http://78.24.223.206:8081/"

    @Singleton
    @Provides
    fun provideSessionManager(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Singleton
    @Provides
    fun providesHttpLogginInterceptor() =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        sessionManager: SessionManager
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val path = originalRequest.url.encodedPath

                // Пропускаем добавление токена для auth-эндпоинтов
                if (path in listOf("/auth/sign-in", "/auth/sign-up")) {
                    return@addInterceptor chain.proceed(originalRequest)
                }

                // Получаем токен из SessionManager
                val token = sessionManager.getAuthToken()

                // Если токен есть, добавляем его в заголовок
                val request = originalRequest.newBuilder()
                    .apply {
                        token?.let {
                            addHeader("Authorization", "Bearer $it")
                        }
                    }
                    .build()

                // Отправляем запрос с добавленным токеном
                chain.proceed(request)
            }
            .addInterceptor(httpLoggingInterceptor) // Логгер запросов
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Singleton
    @Provides
    fun providesPostService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)
}