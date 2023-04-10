package com.example.timemanagementapp.databaseHandling.timeDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User_Time")
class Time (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val record_time: String,
    val label: String,
    @ColumnInfo(name = "work", defaultValue = "College")
    val work: String

)

