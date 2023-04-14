package com.example.timemanagementapp.databaseHandling

import com.google.type.DateTime


data class TimeFirebase(
    val id: Int,
    val time: DateTime,
    val work: String,
    val description: String

)