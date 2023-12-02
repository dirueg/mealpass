package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
@Database(entities = [SignatureEntity::class], version = 1)
abstract class SignatureDatabase : RoomDatabase() {
    abstract fun signatureDao(): SignatureDao
    companion object {
        @Volatile
        private var INSTANCE: SignatureDatabase? = null

        fun getDatabase(context: Context): SignatureDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SignatureDatabase::class.java,
                    "signature_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}