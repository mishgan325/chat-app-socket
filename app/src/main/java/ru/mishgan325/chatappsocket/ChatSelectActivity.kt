package ru.mishgan325.chatappsocket

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatSelectActivity : AppCompatActivity() {

    private val chatList = listOf(
        Chat("Общий чат", "Последнее сообщение..."),
        Chat("Личные сообщения", "Привет!"),
        Chat("Рабочая группа", "Дедлайн завтра")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_select)

        val recyclerView = findViewById<RecyclerView>(R.id.rvChatList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ChatListAdapter(chatList) { chat ->
            openChat(chat)
        }

        findViewById<Button>(R.id.btnNewChat).setOnClickListener {
            startActivity(Intent(this, NewChatActivity::class.java))
        }
    }

    private fun openChat(chat: Chat) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("CHAT_NAME", chat.name)
        }
        startActivity(intent)
    }
}

data class Chat(val name: String, val lastMessage: String)