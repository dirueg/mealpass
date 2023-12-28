package com.example.myapplication
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String
)
@Entity(tableName = "signature_database")
data class SignatureEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userName: String,
    val currentDate: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
    val signature: ByteArray, // Bitmap을 ByteArray로 저장
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SignatureEntity

        if (id != other.id) return false
        if (userName != other.userName) return false
        if (currentDate != other.currentDate) return false
        return signature.contentEquals(other.signature)
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + userName.hashCode()
        result = 31 * result + currentDate.hashCode()
        result = 31 * result + signature.contentHashCode()
        return result
    }
}