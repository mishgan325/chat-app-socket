package ru.mishgan325.chatappsocket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.api.ApiService
import ru.mishgan325.chatappsocket.api.AuthRequest
import ru.mishgan325.chatappsocket.utils.SessionManager


class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var authApi: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)
        authApi = ApiConfig.retrofit.create(ApiService::class.java)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(username, password)) {
                loginUser(username, password)
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInput(username: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                showError("Введите имя пользователя")
                false
            }
            password.isEmpty() -> {
                showError("Введите пароль")
                false
            }
            else -> true
        }
    }

    private fun loginUser(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = authApi.login(AuthRequest(username, password))
                if (response.isSuccessful) {
                    response.body()?.let {
                        sessionManager.saveAuthToken(it.token)
                        startActivity(Intent(this@LoginActivity, ChatSelectActivity::class.java))
                        finish()
                    }
                } else {
                    showError("Неверные учетные данные")
                }
            } catch (e: Exception) {
                showError("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}