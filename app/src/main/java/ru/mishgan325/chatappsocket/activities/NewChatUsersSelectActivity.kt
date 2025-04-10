package ru.mishgan325.chatappsocket.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.adapters.UserAdapter
import ru.mishgan325.chatappsocket.api.ApiService
import ru.mishgan325.chatappsocket.models.User
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager

class NewChatUsersSelectActivity : AppCompatActivity() {

    private lateinit var userAdapter: UserAdapter

    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat_select_users)

        sessionManager = SessionManager(this)
        apiService = ApiConfig.retrofit.create(ApiService::class.java)

        setupRecyclerView()
        loadUsers()
        setupCreateButton()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.rvUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(emptyList())
        recyclerView.adapter = userAdapter
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            try {
                val response = apiService.getUsers()
                if (response.isSuccessful) {
                    response.body()?.let { allUsers ->
                        val users = allUsers.map {
                            User(
                                id = it.id,
                                username = it.username
                            )
                        }
                        Log.d("Chats", users.toString())
                        userAdapter.updateUsers(users)
                    }
                } else {
                    showError("Ошибка: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    private fun setupCreateButton() {
        findViewById<FloatingActionButton>(R.id.fabCreateChat).setOnClickListener {
            val selectedUsers = userAdapter.getSelectedUsers()

            if (selectedUsers.isEmpty()) {
                Toast.makeText(this, "Выберите участников", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("Участники:", selectedUsers.joinToString { it.id.toString() },)
            Log.d("Тип чата:", if (selectedUsers.size > 1) "group-chat" else "private-chat")


            val intent = Intent(this, NewChatNameSelectActivity::class.java).apply {
                putExtra("USER_IDs", selectedUsers.map { it.id } as ArrayList<Long>)
                putExtra("USERNAMES", selectedUsers.map { it.username } as ArrayList<String>)
                putExtra("CHAT_TYPE", if (selectedUsers.size > 1) "GROUP" else "PRIVATE")
            }
            startActivity(intent)
        }
    }


    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("NewChatActivity", message)
    }

}