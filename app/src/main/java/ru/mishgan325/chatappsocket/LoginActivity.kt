package ru.mishgan325.chatappsocket

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (validateCredentials(username, password)) {
                startActivity(Intent(this, ChatSelectActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateCredentials(username: String, password: String): Boolean {
        return true
//        val prefs = getSharedPreferences("user_data", MODE_PRIVATE)
//        return prefs.getString("username", "") == username &&
//                prefs.getString("password", "") == password
    }
}