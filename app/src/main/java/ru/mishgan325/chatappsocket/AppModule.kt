package ru.mishgan325.chatappsocket

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.mishgan325.chatappsocket.api.ApiInterface
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager
import javax.inject.Singleton

// AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApi(context: Context): ApiInterface {
        ApiConfig.initialize(context)
        return ApiConfig.retrofit.create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideSessionManager(context: Context) = SessionManager(context)
}