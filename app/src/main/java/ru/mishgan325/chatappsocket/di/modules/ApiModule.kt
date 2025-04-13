package ru.mishgan325.chatappsocket.di.modules

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
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "http://78.24.223.206:8081/"


    @Singleton
    @Provides
    fun providesHttpLogginInterceptor() =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

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
//    @Singleton
//    @Provides
//    fun providesRetrofit(context: Context): MainApi {
//        ApiConfig.initialize(context)
//        return ApiConfig.retrofit.create(MainApi::class.java)
//    }
//
//    @Singleton
//    @Provides
//    fun provideSessionManager(context: Context) = SessionManager(context)
}