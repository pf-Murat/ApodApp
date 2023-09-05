package com.example.apodapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    @Query("SELECT * FROM apoditem")
    fun getAll(): Flow<List<ApodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg apods: ApodItem)

    @Delete
    suspend fun delete(apod: ApodItem)
}