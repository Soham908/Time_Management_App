package com.example.timemanagementapp.databaseHandling.habitDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HabitTrack")
data class Habit(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val habitName: String,
    val habitDescription: String?,
    val goalDays: Int,
    val habitReminderTime: String?,
    val start_Date: String?,
    val end_Date: String?,
    val frequency: String?,
    @ColumnInfo("habitStatus", defaultValue = "incomplete")
    val habitStatus: String
)
// could add more columns such as current and longest streak

{
}