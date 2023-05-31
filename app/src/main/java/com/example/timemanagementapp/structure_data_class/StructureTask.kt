package com.example.timemanagementapp.structure_data_class


data class StructureTask(
    val taskSubject: String,
    val taskDescription: String?,
    val taskTime: String?,
    val taskPriority: String?
)
//{
//    constructor() : this("", "", "", "")
//}