package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope

@Database(entities = [SignatureEntity::class], version = 1)
abstract class SignatureDatabase : RoomDatabase() {
    abstract fun signatureDao(): SignatureDao
    companion object {
        @Volatile
        private var INSTANCE: SignatureDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SignatureDatabase {
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