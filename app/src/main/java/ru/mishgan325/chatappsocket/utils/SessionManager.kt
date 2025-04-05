package ru.mishgan325.chatappsocket.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {
    private val prefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Сохранение данных
    fun saveAuthToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun saveUsername(username: String) {
        prefs.edit().putString("username", username).apply()
    }

    // Получение данных
    fun getAuthToken(): String? = prefs.getString("jwt_token", null)
    fun getUsername(): String? = prefs.getString("username", null)


    fun isLoggedIn(): Boolean {
        val token = getAuthToken()
        val isLogged = token != null
        Log.d("Auth", "isLoggedIn: $isLogged")
        return isLogged
    }

    fun logout() {
        prefs.edit().remove("jwt_token").apply()
    }
}