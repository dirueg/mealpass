package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
public interface SignatureDao {
    @Insert
    suspend fun insertSignature(signature: SignatureEntity)

    @Query("SELECT * FROM signature_database ORDER BY currentDate DESC")
    fun getAllSignatures(): LiveData<List<SignatureEntity>>
}