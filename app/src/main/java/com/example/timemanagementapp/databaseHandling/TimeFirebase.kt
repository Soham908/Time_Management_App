package com.example.timemanagementapp.databaseHandling

import com.google.type.DateTime


data class TimeFirebase(
    val id: Int,
    val time: String,
    val work: String,
    val description: String,
    val Tasks: Map<String, Any>

)
{
    constructor() : this(0, "", "", "", emptyMap())
}