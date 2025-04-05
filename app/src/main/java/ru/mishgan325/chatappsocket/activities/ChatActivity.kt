package ru.mishgan325.chatappsocket.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.mishgan325.chatappsocket.adapters.ChatAdapter
import ru.mishgan325.chatappsocket.R

class ChatActivity : AppCompatActivity() {

    private val messages = mutableListOf(
        Message("Анна", "Привет! Как дела?", false),
        Message("Я", "Отлично, спасибо!", true),
        Message("Анна", "Что нового?", false)
    )

    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatName = intent.getStringExtra("CHAT_NAME") ?: "Чат"
        findViewById<TextView>(R.id.tvChatTitle).text = chatName

        setupRecyclerView()
        setupSendButton()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.rvMessages)
        adapter = ChatAdapter(messages)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSendButton() {
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<Button>(R.id.btnSend)
        val btnAttach = findViewById<Button>(R.id.btnAttach)

        btnSend.setOnClickListener {
            val messageText = etMessage.text.toString()
            if (messageText.isNotEmpty()) {
                messages.add(Message("Я", messageText, true))
                adapter.notifyItemInserted(messages.size - 1)
                etMessage.text.clear()
                recyclerView.smoothScrollToPosition(messages.size - 1)
            }
        }

        btnAttach.setOnClickListener {
            // TODO: Реализовать выбор файла
            Toast.makeText(this, "Выбор файла", Toast.LENGTH_SHORT).show()
        }
    }
}

data class Message(
    val sender: String,
    val text: String,
    val isMine: Boolean
)