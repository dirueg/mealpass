package com.example.myapplication
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "signature_database")
data class SignatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val signature: ByteArray, // Bitmap을 ByteArray로 저장
    val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)