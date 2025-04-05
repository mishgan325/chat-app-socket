package ru.mishgan325.chatappsocket.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.adapters.ChatAdapter
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.api.ApiService
import ru.mishgan325.chatappsocket.models.Message
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager


class ChatActivity : AppCompatActivity() {
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sessionManager: SessionManager
    private lateinit var chatApi: ApiService
    private var chatId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sessionManager = SessionManager(this)
        chatApi = ApiConfig.retrofit.create(ApiService::class.java)
        chatId = intent.getStringExtra("CHAT_ID") ?: ""

        val chatName = intent.getStringExtra("CHAT_NAME") ?: "Чат"
        findViewById<TextView>(R.id.tvChatTitle).text = chatName

        setupRecyclerView()
        setupSendButton()
        loadMessages()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvMessages)
        adapter = ChatAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadMessages() {
        Log.d("ChatActivity", "Loading messages for chatId: $chatId")
        if (chatId.isEmpty()) {
            Toast.makeText(this, "Ошибка: не указан ID чата", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = chatApi.getChatMessages(chatId)
                Log.d("ChatActivity", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    response.body()?.let { messagesDto ->
                        Log.d("ChatActivity", "Received ${messagesDto.size} messages")

                        val currentUser = sessionManager.getUsername().toString()

                        val messages = messagesDto.map { dto ->
                            Message(
                                id = dto.id,
                                sender = Message.Sender(
                                    id = dto.sender.id,
                                    username = dto.sender.username
                                ),
                                content = dto.content,
                                fileUrl = dto.fileUrl,
                                timestamp = dto.timestamp,
                                isMine = dto.sender.username == currentUser
                            )
                        }

                        runOnUiThread {
                            adapter.updateMessages(messages)
                            recyclerView.smoothScrollToPosition(messages.size - 1)
                        }
                    }
                } else {
                    Log.e("ChatActivity", "Error response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ChatActivity", "Error loading messages", e)
                runOnUiThread {
                    Toast.makeText(
                        this@ChatActivity,
                        "Ошибка загрузки: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setupSendButton() {
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
//                        val response = chatApi.sendMessage(
//                            chatId,
//                            SendMessageRequest(messageText)
//
//                            if (response.isSuccessful) {
//                                runOnUiThread {
//                                    etMessage.text.clear()
//                                    loadMessages() // Перезагружаем сообщения после отправки
//                                }
//                            } else {
//                                Log.e("ChatActivity", "Send error: ${response.code()}")
//                            }
                    } catch (e: Exception) {
                        Log.e("ChatActivity", "Send failed", e)
                    }
                }
            }
        }
    }
}
