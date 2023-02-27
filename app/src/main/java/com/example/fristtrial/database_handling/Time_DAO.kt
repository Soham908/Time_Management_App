package com.example.fristtrial.database_handling

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface Time_DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(time: User_Time)

    @Update
    suspend fun update(time: User_Time)

    @Delete
    suspend fun delete(time: User_Time)

    @Query("SELECT * FROM user ORDER BY uid ASC")
    fun getAllUsers(): LiveData<List<User_Time>>

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}