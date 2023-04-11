package com.example.timemanagementapp.databaseHandling.todoDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ToDoList")
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val taskSubject: String?,
    val taskDescription: String,
    @ColumnInfo("taskPriority", defaultValue = "Not Important")
    val taskPriority: String?,
    @ColumnInfo(name = "taskStatus", defaultValue = "False")
    val taskStatus: String
)
{

}