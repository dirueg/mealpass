package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}

@Dao
public interface SignatureDao {
    @Insert
    suspend fun insertSignature(signature: SignatureEntity)

    @Query("SELECT * FROM signature_database ORDER BY currentDate DESC")
    fun getAllSignatures(): LiveData<List<SignatureEntity>>
}