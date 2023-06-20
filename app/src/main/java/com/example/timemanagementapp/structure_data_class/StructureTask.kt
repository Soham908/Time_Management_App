package com.example.timemanagementapp.structure_data_class


data class StructureTask(
    var taskSubject: String,
    var taskDescription: String?,
    var taskTime: String?,
    val taskPriority: String?
)
//{
//    constructor() : this("", "", "", "")
//}