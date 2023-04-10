package com.example.timemanagementapp.databaseHandling

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val exercise_name: String,
    val sets: String,
    val reps: String
)
{
}