package com.example.timemanagementapp.databaseHandling.habitDB

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HabitDAO {

    @Query("select * from HabitTrack where habitStatus= 'incomplete' ")
    fun getPendingHabits(): LiveData<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewHabits(habits: Habit)

    @Query("delete HabitTrack where id= :habitId ")
    suspend fun deleteHabit(habitId: Int)

    @Query("update HabitTrack where id= :habitId ")
    suspend fun updateHabit(habitId: Int)

}