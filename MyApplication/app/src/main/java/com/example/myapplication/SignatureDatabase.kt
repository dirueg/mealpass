package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database(entities = [SignatureEntity::class], version = 1)
class SignatureDatabase : RoomDatabase() {
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

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }

    fun signatureDao(): SignatureDao {
        return TODO("Provide the return value")
    }
}