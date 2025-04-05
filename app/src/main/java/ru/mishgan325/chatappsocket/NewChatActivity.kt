package ru.mishgan325.chatappsocket

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NewChatActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    private val allUsers = listOf(
        User("Анна"),
        User("Борис"),
        User("Виктор"),
        User("Галина"),
        User("Дмитрий"),
        User("Михаил"),
        User("Иван"),
        User("Яков"),
        User("Георгий"),
        User("Михаил"),
        User("Иван"),
        User("Яков"),
        User("Георгий")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat)

        setupRecyclerView()
        setupCreateButton()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvUsers)
        adapter = UserAdapter(allUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupCreateButton() {
        findViewById<Button>(R.id.btnCreateChat).setOnClickListener {
            val chatName = findViewById<EditText>(R.id.etChatName).text.toString()
            val selectedUsers = adapter.getSelectedUsers()

            if (chatName.isEmpty()) {
                Toast.makeText(this, "Введите название чата", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "Выберите участников", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Логирование для дебага
            println("Создан чат: $chatName")
            println("Участники: ${selectedUsers.joinToString { it.name }}")
            println("Тип чата: ${if (selectedUsers.size > 1) "group-chat" else "private-chat"}")

            finish()
        }
    }
}