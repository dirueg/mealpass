package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<User>>

    @Insert
    fun insert(user: User)

    @Delete
    fun delete(user: User)
}

@Dao
public interface SignatureDao {
    @Insert
    fun insertSignature(signature: SignatureEntity)

    @Query("SELECT * FROM signature_database")
    fun getAllSignatures(): LiveData<List<SignatureEntity>>

    @Query("SELECT * FROM signature_database WHERE currentDate BETWEEN :startDate AND :endDate")
    fun getSignaturesInRange(startDate: String, endDate: String): LiveData<List<SignatureEntity>>
    @Query("SELECT * FROM signature_database WHERE currentDate BETWEEN :startDate AND :endDate ORDER BY currentDate")
    fun getSignaturesSortedByDate(startDate: String, endDate: String): LiveData<List<SignatureEntity>>

    @Query("SELECT * FROM signature_database WHERE currentDate BETWEEN :startDate AND :endDate ORDER BY userName")
    fun getSignaturesSortedByName(startDate: String, endDate: String): LiveData<List<SignatureEntity>>

    @Query("SELECT * FROM signature_database WHERE userName LIKE :name ORDER BY currentDate")
    fun getSignaturesByName(name: String): LiveData<List<SignatureEntity>>

    @Query("SELECT * FROM signature_database WHERE userName LIKE :name and currentDate LIKE :date ORDER BY currentDate")
    fun getSignaturesByNameAndDate(name: String, date: String): List<SignatureEntity>

    @Query("delete from signature_database WHERE id in (:signatureIds)")
    fun deleteSignatures(signatureIds: List<Int>)
}