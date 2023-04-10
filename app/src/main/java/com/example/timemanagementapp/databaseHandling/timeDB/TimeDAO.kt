package com.example.timemanagementapp.databaseHandling.timeDB

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimeDAO {

    @Insert()
    suspend fun insertTimeDetails(time: Time)

    @Query("select * from User_Time")
    fun getTimeInfo(): LiveData<List<Time>>

    @Delete
    suspend fun deleteTimeDetails(time: Time)

    @Update
    suspend fun updateTimeDetails(time: Time)
}