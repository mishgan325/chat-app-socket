package ru.mishgan325.chatappsocket.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.adapters.ChatListAdapter
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.api.ApiService
import ru.mishgan325.chatappsocket.models.Chat
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager

class ChatSelectActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatListAdapter
    private lateinit var authApi: ApiService
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка авторизации перед установкой контента
        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        setContentView(R.layout.activity_chat_select)

        // Инициализация зависимостей
        authApi = ApiConfig.retrofit.create(ApiService::class.java)

        setupRecyclerView()
        loadChatRooms()

        findViewById<Button>(R.id.btnNewChat).setOnClickListener {
            startActivity(Intent(this, NewChatActivity::class.java))
        }
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Закрываем текущую активность чтобы нельзя было вернуться назад
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvChatList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatListAdapter(emptyList()) { chat:Chat ->
            openChat(chat)
        }
        recyclerView.adapter = chatAdapter
    }

    private fun loadChatRooms() {
        lifecycleScope.launch {
            try {
                val response = authApi.getMyChatRooms()
                if (response.isSuccessful) {
                    response.body()?.let { chatRooms ->
                        val chats = chatRooms.map {
                            Chat(
                                id = it.id,
                                name = it.name,
                                type = it.type
                            )
                        }
                        chatAdapter.updateChats(chats)
                    }
                } else {
                    showError("Ошибка: ${response.code()}")
                    redirectToLogin()
                }
            } catch (e: Exception) {
                showError("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    private fun openChat(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("CHAT_NAME", chat.name)
            putExtra("CHAT_ID", chat.id)
        }
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}