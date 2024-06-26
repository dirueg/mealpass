package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.myapplication.ui.SignatureView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Singleton init
        DatabaseSingleton(applicationContext)

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dateTextView.text = currentDate
        val appDB = DatabaseSingleton.AppDB

        val gridLayout: GridLayout = findViewById(R.id.idButton)
        val userDao = appDB.userDao()

        // LiveData 관찰
        userDao.getAll().observe(this, Observer { users ->
            gridLayout.removeAllViews() // 이전 버튼 제거
            users.forEach { user ->
                val button = Button(this@MainActivity)
                button.text = user.name
                button.textSize = 32F
                val drawableResource = R.drawable.name_button
                button.setBackgroundResource(drawableResource)
                button.setOnClickListener {
                    val intent = Intent(this@MainActivity, SignatureView.Popup::class.java)
                    intent.putExtra("userName", user.name)
                    startActivity(intent)
                }

                // GridLayout.LayoutParams 설정
                val params = GridLayout.LayoutParams()
                params.width = GridLayout.LayoutParams.WRAP_CONTENT
                params.height = GridLayout.LayoutParams.WRAP_CONTENT
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                params.setMargins(8, 8, 8, 8)
                button.layoutParams = params

                gridLayout.addView(button, params)
            }
        })



        val managermodebutton: Button = findViewById(R.id.ManagerMode)
        managermodebutton.setOnClickListener {
            startActivity(Intent(this, manager_page::class.java))
        }
    }

//    fun showPasswordDialog() {
//        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null)
//        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)
//
//        AlertDialog.Builder(this)
//            .setTitle("비밀번호 입력")
//            .setView(dialogView)
//            .setPositiveButton("확인") { dialog, which ->
//                val password = passwordEditText.text.toString()
//                if (password == "1234") {
//                    startActivity(Intent(this, manager_page::class.java))
//                } else {
//                    Toast.makeText(this, "잘못된 비밀번호입니다", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("취소", null)
//            .show()
//    }
}
