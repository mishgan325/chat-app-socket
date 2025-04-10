package ru.mishgan325.chatappsocket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mishgan325.chatappsocket.R
import ru.mishgan325.chatappsocket.models.User

class UserAdapter(private var users: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val selectedUsers = mutableSetOf<User>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cbUser: CheckBox = view.findViewById(R.id.cbUser)
        private val tvUserName: TextView = view.findViewById(R.id.tvUserName)

        fun bind(user: User) {
            tvUserName.text = user.username
            cbUser.isChecked = selectedUsers.contains(user)

            cbUser.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedUsers.add(user) else selectedUsers.remove(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun getSelectedUsers() = selectedUsers.toList()

//    fun getSelectedUsers() = selectedUsers.map { it.id }

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}