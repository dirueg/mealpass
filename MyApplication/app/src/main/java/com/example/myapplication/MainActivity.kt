package com.example.myapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.Dao
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream



class SignatureView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private var path = Path()
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 4f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
            }
            else -> return false
        }
        postInvalidate()
        return false
    }

    fun clear() {
        path.reset()
        postInvalidate()
    }

    fun getSignatureBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
}


class MainActivity() : AppCompatActivity() {
    private lateinit var signatureView: SignatureView
    private lateinit var saveSignatureButton: Button
    private val signatureDao: SignatureDao by lazy {
        SignatureDatabase.getDatabase(this).signatureDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dateTextView.text = currentDate

        val toggleContainer: LinearLayout = findViewById(R.id.toggleContainer)
        val userNames = arrayOf("사용자1", "사용자2", "사용자3")
        userNames.forEach { name ->
            val toggleButton = ToggleButton(this)
            toggleButton.text = name
            toggleButton.textOn = name
            toggleButton.textOff = name
            toggleContainer.addView(toggleButton)
        }

        val confirmButton: Button = findViewById(R.id.confirmButton)
        confirmButton.setOnClickListener {
            // 버튼 클릭 시 수행할 작업
        }

        val managermodebutton: Button = findViewById(R.id.ManagerMode)
        managermodebutton.setOnClickListener {
            showPasswordDialog()
        }

        signatureView = findViewById(R.id.signatureView)
        saveSignatureButton = findViewById(R.id.saveSignatureButton)
        saveSignatureButton.setOnClickListener {
            saveSignature()
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

    private fun saveSignature() {
        val bitmap = signatureView.getSignatureBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        bitmap.recycle()
        val signatureEntity = SignatureEntity(
            userName = "User", // 괄호 추가
            signature = byteArray
        )
        CoroutineScope(Dispatchers.IO).launch {
            signatureDao.insertSignature(signatureEntity)
        }
    }
/*
    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }
 */
}
