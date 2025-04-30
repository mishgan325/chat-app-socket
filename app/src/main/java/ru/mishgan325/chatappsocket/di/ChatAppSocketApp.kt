package ru.mishgan325.chatappsocket.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatAppSocketApp : Application() {
    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.d("ActivityTrace", "Создана: ${activity.localClassName}")
            }

            override fun onActivityStarted(activity: Activity) {
                Log.d("ActivityTrace", "Старт: ${activity.localClassName}")
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d("ActivityTrace", "Резюмирована: ${activity.localClassName}")
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d("ActivityTrace", "Пауза: ${activity.localClassName}")
            }

            override fun onActivityStopped(activity: Activity) {
                Log.d("ActivityTrace", "Остановлена: ${activity.localClassName}")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                // Можно ничего не делать
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.d("ActivityTrace", "Уничтожена: ${activity.localClassName}")
            }
        })
    }
}