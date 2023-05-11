package com.example.timemanagementapp.recyclerviewAdapter.todo


data class StructureTask(
    val taskSubject: String,
    val taskDescription: String?,
    val taskTime: String?,
    val taskPriority: String?
)
//{
//    constructor() : this("", "", "", "")
//}