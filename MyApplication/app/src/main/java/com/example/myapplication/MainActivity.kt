package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
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
}
