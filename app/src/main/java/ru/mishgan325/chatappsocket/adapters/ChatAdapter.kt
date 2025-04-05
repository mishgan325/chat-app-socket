package ru.mishgan325.chatappsocket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.activities.Message

class ChatAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isMine) VIEW_TYPE_MY_MESSAGE else VIEW_TYPE_OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> MyMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_item_right, parent, false)
            )
            else -> OtherMessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.message_item_left, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is MyMessageViewHolder -> holder.bind(message)
            is OtherMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: Message) {
            itemView.findViewById<TextView>(R.id.tvMessage).text = message.text
        }
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: Message) {
            itemView.findViewById<TextView>(R.id.tvSender).text = message.sender
            itemView.findViewById<TextView>(R.id.tvMessage).text = message.text
        }
    }
}