package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
public interface SignatureDao {
    @Insert
    suspend fun insertSignature(signature: SignatureEntity)

    @Query("SELECT * FROM signatureentity ORDER BY timestamp DESC")
    fun getAllSignatures(): LiveData<List<SignatureEntity>>
}