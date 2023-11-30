package com.example.myapplication
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SignatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val signature: ByteArray, // Bitmap을 ByteArray로 저장
    val timestamp: Long = System.currentTimeMillis()
)