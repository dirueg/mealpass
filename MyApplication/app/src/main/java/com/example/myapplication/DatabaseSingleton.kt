package com.example.myapplication

import android.content.Context

class DatabaseSingleton {
    companion object{
        lateinit var AppDB: AppDatabase
        lateinit var SignDB: SignatureDatabase
    }
    constructor(context: Context) {
        AppDB = AppDatabase.getDatabase(context)
        SignDB = SignatureDatabase.getDatabase(context)
    }
}