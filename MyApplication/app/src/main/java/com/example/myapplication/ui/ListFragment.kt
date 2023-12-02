package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.AppDatabase
import com.example.myapplication.DatabaseSingleton
import com.example.myapplication.R
import com.example.myapplication.User
import com.example.myapplication.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListFragment : Fragment() {
    private lateinit var namesAdapter: NamesAdapter
    private lateinit var userDao: UserDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.list_fragment, container, false)
        val namesRecyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        val addNameButton: Button = view.findViewById(R.id.addNameButton)
        val nameEditText: EditText = view.findViewById(R.id.nameEditText)

        val db = DatabaseSingleton.AppDB
        userDao = db.userDao()

        namesAdapter = NamesAdapter { position ->
            CoroutineScope(Dispatchers.IO).launch {
                userDao.delete(User(id = position, name = ""))
            }
        }

        namesRecyclerView.layoutManager = LinearLayoutManager(context)
        namesRecyclerView.adapter = namesAdapter

        userDao.getAll().observe(viewLifecycleOwner, Observer { users ->
            namesAdapter.setUsers(users)
        })

        addNameButton.setOnClickListener {
            val name = nameEditText.text.toString()
            if (name.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    userDao.insert(User(name = name))
                }
                nameEditText.text.clear()
            } else {
                Toast.makeText(context, "Please enter a name to add", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}

class NamesAdapter(private val onItemRemoved: (Int) -> Unit) : RecyclerView.Adapter<NamesAdapter.ViewHolder>() {
    private var users = emptyList<User>()

    fun setUsers(users: List<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_name, parent, false)
        return ViewHolder(view, onItemRemoved)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    class ViewHolder(itemView: View, private val onItemRemoved: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(user: User) {
            val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
            itemNameTextView.text = user.name

            itemView.findViewById<Button>(R.id.deleteButton).setOnClickListener {
                onItemRemoved(user.id)
            }
        }
    }
}