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

public class SignatureView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
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
public class Popup : AppCompatActivity() {
    private lateinit var signatureView: SignatureView
    private lateinit var saveSignatureButton: Button
    private lateinit var textView: TextView
    val name = intent.getStringExtra("userName")
//    textView.text = "Hello, $name!"
//    final TextView helloTextView = (TextView) findViewById(R.id.text_view_id);
//    helloTextView.setText(R.string.user_greeting);
    private val signatureDao: SignatureDao by lazy {
        SignatureDatabase.getDatabase(this, CoroutineScope(SupervisorJob())).signatureDao()
    }
    @Override
//    protected void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.popup);
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup)


        signatureView = findViewById(R.id.signatureView)
        saveSignatureButton = findViewById(R.id.saveSignatureButton)
        saveSignatureButton.setOnClickListener {
            saveSignature()
        }
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
            Log.d("[SaveSignature]: ", signatureEntity.userName + ", " + signatureEntity.currentDate +".."+ signatureEntity.signature)
        }
    }

    fun clear() {
        signatureView.clear()
    }
}









}
/*
public class SignatureView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
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
 */