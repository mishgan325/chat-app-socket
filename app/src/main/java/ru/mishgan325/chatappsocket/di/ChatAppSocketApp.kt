package ru.mishgan325.chatappsocket.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatAppSocketApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}