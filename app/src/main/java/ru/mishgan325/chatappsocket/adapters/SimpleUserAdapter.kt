package ru.mishgan325.chatappsocket.adapters

import androidx.recyclerview.widget.RecyclerView
import ru.mishgan325.chatappsocket.models.User

// Простейший адаптер только для отображения списка
class SimpleUserAdapter(private val users: List<String>) :
    RecyclerView.Adapter<SimpleUserAdapter.ViewHolder>() {

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val textView: android.widget.TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = users[position]
    }

    override fun getItemCount() = users.size
}