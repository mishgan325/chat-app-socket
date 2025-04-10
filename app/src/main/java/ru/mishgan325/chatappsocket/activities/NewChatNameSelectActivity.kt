package ru.mishgan325.chatappsocket.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.adapters.ChatListAdapter
import ru.mishgan325.chatappsocket.adapters.SimpleUserAdapter
import ru.mishgan325.chatappsocket.api.ApiService
import ru.mishgan325.chatappsocket.api.AuthRequest
import ru.mishgan325.chatappsocket.api.CreateGroupChatRequest
import ru.mishgan325.chatappsocket.api.CreatePrivateChatRequest
import ru.mishgan325.chatappsocket.models.Chat
import ru.mishgan325.chatappsocket.models.User
import ru.mishgan325.chatappsocket.utils.ApiConfig
import ru.mishgan325.chatappsocket.utils.SessionManager

class NewChatNameSelectActivity : AppCompatActivity() {

    private lateinit var chatName: String
    private lateinit var userIds: List<Long>
    private lateinit var usernames: List<String>
    private lateinit var etChatName: TextInputEditText
    private lateinit var fabCreate: ExtendedFloatingActionButton
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private lateinit var chatType: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_chat_select_name)


        sessionManager = SessionManager(this)
        apiService = ApiConfig.retrofit.create(ApiService::class.java)

        userIds = intent.getSerializableExtra("USER_IDs") as? ArrayList<Long> ?: emptyList<Long>()
        usernames =
            intent.getSerializableExtra("USERNAMES") as? ArrayList<String> ?: emptyList<String>()
        chatType = intent.getStringExtra("CHAT_TYPE").toString()

        Log.d("USER_IDs", userIds.toString())
        Log.d("USERNAMES", usernames.toString())
        Log.d("TYPE", chatType)

        initViews()
        setupInitialChatName(usernames)
        setupClickListeners()
    }

    private fun initViews() {
        etChatName = findViewById(R.id.etChatName)
        fabCreate = findViewById(R.id.fabCreate)

        findViewById<RecyclerView>(R.id.rvUsers).apply {
            layoutManager = LinearLayoutManager(this@NewChatNameSelectActivity)
            adapter =
                SimpleUserAdapter(usernames)
        }
    }

    private fun setupInitialChatName(users: List<String>) {
        val initialName = sessionManager.getUsername() + ", " + users.joinToString { it }
        etChatName.apply {
            setText(initialName)
            setSelection(initialName.length)
        }
    }

    private fun setupClickListeners() {
        fabCreate.setOnClickListener {
            chatName = etChatName.text.toString().trim()

            if (chatName.isEmpty()) {
                Toast.makeText(this, "Введите название чата", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (chatType == "PRIVATE") {
                createPrivateChat()
            } else {
                createGroupChat()
            }
        }
    }

    private fun createPrivateChat() {
        lifecycleScope.launch {
            try {
                val response =
                    apiService.createPrivateChat(CreatePrivateChatRequest(chatName, userIds[0]))
                if (response.isSuccessful) {
                    finishAfterCreateChat()
                } else {
                    showError("Ошибка: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    private fun createGroupChat() {
        lifecycleScope.launch {
            try {
                val response =
                    apiService.createGroupChat(CreateGroupChatRequest(chatName, userIds))
                if (response.isSuccessful) {
                    finishAfterCreateChat()
                } else {
                    showError("Ошибка: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    private fun finishAfterCreateChat() {
        val intent = Intent(this, ChatSelectActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("NewChatNameSelectActivity", message)
    }
}