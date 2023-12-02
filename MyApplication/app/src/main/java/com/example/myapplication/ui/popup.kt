package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.SignatureDao
import com.example.myapplication.SignatureDatabase
import com.example.myapplication.SignatureEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    fun getSignatureBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }
    fun clear() {
        path.reset()
        postInvalidate()
    }

 class Popup() : AppCompatActivity() {
    private lateinit var signatureView: SignatureView
    private lateinit var saveSignatureButton: Button
    private lateinit var textView: TextView
//    private lateinit var name: String

    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val signatureDao: SignatureDao by lazy {
        SignatureDatabase.getDatabase(this, CoroutineScope(SupervisorJob())).signatureDao()
    }

    private fun saveSignature() {
        setContentView(R.layout.popup)
        val bitmap = signatureView.getSignatureBitmap()
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        bitmap.recycle()
        val signatureEntity = SignatureEntity(
            userName = "User", // 괄호 추가
            signature = byteArray
        )
        Log.d("[SaveSignature]: ", "${signatureEntity.userName}, ${signatureEntity.currentDate}, ${signatureEntity.signature.contentToString()}")

        // Log.d("[SaveSignature]: ", signatureEntity.userName + ", " + signatureEntity.currentDate +".."+ signatureEntity.signature)

        CoroutineScope(Dispatchers.IO).launch {
            signatureDao.insertSignature(signatureEntity)
           }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup)

        val userName = intent.getStringExtra("userName") ?: "Unknown" // null 처리
        signatureView = findViewById(R.id.signatureView)
        saveSignatureButton = findViewById(R.id.saveSignatureButton)
        saveSignatureButton.setOnClickListener {
            Log.d("setOnClickListener", "saveSignature on")
            saveSignature()
        }
    }

    fun clear() {
        signatureView.clear()
    }
}


}
