package ru.mishgan325.chatappsocket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.api.ApiService
import ru.mishgan325.chatappsocket.api.RegisterRequest
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var authApi: ApiService
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authApi = ApiConfig.retrofit.create(ApiService::class.java)
        sessionManager = SessionManager(this)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, username, password)) {
                registerUser(email, username, password)
            }
        }
    }

    private fun validateInput(email: String, username: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                showError("Введите email")
                false
            }
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

    private fun registerUser(email: String, username: String, password: String) {
        lifecycleScope.launch {
            try {
                val request = RegisterRequest(username, email, password)
                val response = authApi.register(request)

                if (response.isSuccessful) {
                    response.body()?.let {
                        sessionManager.saveAuthToken(it.token)
                        startActivity(Intent(this@RegisterActivity, ChatSelectActivity::class.java))
                        finish()
                    }
                } else {
                    showError("Ошибка регистрации: ${response.code()}")
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