package com.example.timemanagementapp.databaseHandling


data class TaskFirebase(
    val taskSubject: String,
    val taskDescription: String,
    val taskTime: String,
    val taskPriority: String
)
{
    constructor() : this("", "", "", "")
}