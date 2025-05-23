package ru.mishgan325.chatappsocket.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {
    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Сохранение данных
    fun saveAuthToken(token: String) {
        prefs.edit { putString("jwt_token", token) }
    }

    fun saveUsername(username: String) {
        prefs.edit { putString("username", username) }
    }

    fun saveUserId(id: Long) = prefs.edit { putLong("user_id", id) }

    // Получение данных
    fun getAuthToken(): String? = prefs.getString("jwt_token", null)
    fun getUsername(): String? = prefs.getString("username", null)
    fun getUserId(): Long? = prefs.getLong("user_id", -1).takeIf { it != -1L }

    fun isLoggedIn(): Boolean {
        val token = getAuthToken()
        val isLogged = token != null
        Log.d("Auth", "isLoggedIn: $isLogged")
        return isLogged
    }

    fun logout() {
        prefs.edit() {
            remove("jwt_token")
                .remove("username")
                .remove("userId")
        }
    }
}