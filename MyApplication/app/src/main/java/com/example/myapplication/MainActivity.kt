package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.myapplication.ui.SignatureView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity() : AppCompatActivity() {
    private lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dateTextView.text = currentDate
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

//        val userDao = db.userDao()
//        val users = userDao.getAll()

        val gridLayout: GridLayout = findViewById(R.id.idButton)
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = db.userDao()
            val users = userDao.getAll()
            runOnUiThread {
                users.forEach { user ->
                    val button = Button(this@MainActivity)
                    button.text = user.name
                    button.textSize = 32F
                    button.setOnClickListener {
                        val intent = Intent(this@MainActivity, SignatureView.Popup::class.java)
                        intent.putExtra("userName", user.name)
                        startActivity(intent)
                    }
//        users.forEach { name ->
//            val Button = Button(this)
//            Button.text = users.toString()
//            Button.textSize = 32F
//            Button.setOnClickListener {
//                val intent = Intent(this, SignatureView.Popup::class.java)
//                intent.putExtra("userName", name)
//                startActivity(intent)
//            }

                    // GridLayout.LayoutParams 설정
                    val params = GridLayout.LayoutParams()
                    params.width = 0 // 너비를 0으로 설정하여 가중치가 작동하도록 함
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f) // 열 가중치를 1로 설정
                    params.setMargins(8, 8, 8, 8) // 필요한 경우 마진 설정
                    button.layoutParams = params
                    gridLayout.addView(button, params)

                }

                val confirmButton: Button = findViewById(R.id.confirmButton)
                confirmButton.setOnClickListener {
                    // 버튼 클릭 시 수행할 작업
                }

                val managermodebutton: Button = findViewById(R.id.ManagerMode)
                managermodebutton.setOnClickListener {
                    showPasswordDialog()

                }


            }

        }

    }

    fun showPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.passwordEditText)

        AlertDialog.Builder(this)
            .setTitle("비밀번호 입력")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, which ->
                val password = passwordEditText.text.toString()
                if (password == "1234") {
                    startActivity(Intent(this, manager_page::class.java))
                } else {
                    Toast.makeText(this, "잘못된 비밀번호입니다", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }
    override fun onDestroy() {
        super.onDestroy()
        db.close() // 데이터베이스 인스턴스 닫기
    }
}

