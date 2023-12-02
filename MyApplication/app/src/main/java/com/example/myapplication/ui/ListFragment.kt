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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ListFragment : Fragment() {

    private lateinit var namesAdapter: NamesAdapter
    private val namesList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.list_fragment, container, false)

        val namesRecyclerView: RecyclerView = view.findViewById(R.id.namesRecyclerView)
        val addNameButton: Button = view.findViewById(R.id.addNameButton)
        val nameEditText: EditText = view.findViewById(R.id.nameEditText)

        namesAdapter = NamesAdapter(namesList) { position ->
            removeName(position)
        }

        namesRecyclerView.layoutManager = LinearLayoutManager(context)
        namesRecyclerView.adapter = namesAdapter

        addNameButton.setOnClickListener {
            val name = nameEditText.text.toString()
            if (name.isNotEmpty()) {
                namesList.add(name)
                namesAdapter.notifyItemInserted(namesList.size - 1)
                nameEditText.text.clear()
            } else {
                Toast.makeText(context, "추가하려는 직원의 이름을 먼저 적어주세요!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun removeName(position: Int) {
        namesList.removeAt(position)
        namesAdapter.notifyItemRemoved(position)
    }
}

class NamesAdapter(private val names: MutableList<String>, private val onItemRemoved: (Int) -> Unit) : RecyclerView.Adapter<NamesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_name, parent, false)
        return ViewHolder(view, onItemRemoved)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(names[position])
    }

    override fun getItemCount(): Int = names.size

    class ViewHolder(itemView: View, private val onItemRemoved: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        fun bind(name: String) {
            // 예를 들어 TextView의 ID가 itemNameTextView 라고 가정
            val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
            itemNameTextView.text = name

            // 아이템 삭제 로직
            itemView.findViewById<Button>(R.id.deleteButton).setOnClickListener {
                onItemRemoved(adapterPosition)
            }
        }
    }
}