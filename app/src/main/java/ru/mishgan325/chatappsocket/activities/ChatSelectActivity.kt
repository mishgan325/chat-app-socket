package ru.mishgan325.chatappsocket.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.adapters.ChatListAdapter
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.api.ApiService
import ru.mishgan325.chatappsocket.api.AuthRequest
import ru.mishgan325.chatappsocket.models.Chat
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager

class ChatSelectActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatListAdapter
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Проверка авторизации перед установкой контента
        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            logout()
            return
        }

        // TODO: regenerate token
        // update username
        // update id

        // Инициализация зависимостей
        apiService = ApiConfig.retrofit.create(ApiService::class.java)

        whoami()

        setContentView(R.layout.activity_chat_select)


        setupRecyclerView()
        loadChatRooms()

        findViewById<FloatingActionButton>(R.id.fabAddChat).setOnClickListener {
            startActivity(Intent(this, NewChatActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                sessionManager.logout()
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun whoami() {
        lifecycleScope.launch {
            try {
                val response = apiService.whoami()
                if (response.isSuccessful) {
                    response.body()?.let {
                        sessionManager.saveUserId(it.id)
                        sessionManager.saveUsername(it.username)
                    }
                } else {
                    showError("Перезайдите")
                }
            } catch (e: Exception) {
                showError("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    private fun logout() {
        sessionManager.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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
                val response = apiService.getMyChatRooms()
                if (response.isSuccessful) {
                    response.body()?.let { chatRooms ->
                        val chats = chatRooms.map {
                            Chat(
                                id = it.id,
                                name = it.name,
                                type = it.type
                            )
                        }
                        Log.d("Chats", chats.toString())
                        chatAdapter.updateChats(chats)
                    }
                } else {
                    showError("Ошибка: ${response.code()}")
                    logout()
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
        Log.e("Chat select activity", message)
    }

}