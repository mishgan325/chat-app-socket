package ru.mishgan325.chatappsocket

import android.app.Application
import ru.mishgan325.chatappsocket.utils.ApiConfig

class ChatAppSocket : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiConfig.initialize(this) // Важный вызов!
    }
}