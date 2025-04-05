package ru.mishgan325.chatappsocket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.models.Chat

class ChatListAdapter(
    private var chats: List<Chat>,
    private val onItemClick: (Chat) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.findViewById(R.id.tvChatName)
        private val typeView: TextView = view.findViewById(R.id.tvChatType)

        fun bind(chat: Chat) {
            nameView.text = chat.name
            typeView.text = when (chat.type) {
                "PRIVATE" -> "Личный чат"
                "GROUP" -> "Групповой чат"
                else -> "Неизвестный тип"
            }

            itemView.setOnClickListener { onItemClick(chat) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size

    fun updateChats(newChats: List<Chat>) {
        chats = newChats
        notifyDataSetChanged()
    }
}