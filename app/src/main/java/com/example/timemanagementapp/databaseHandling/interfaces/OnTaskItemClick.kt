package com.example.timemanagementapp.databaseHandling.interfaces

import com.example.timemanagementapp.recyclerviewAdapter.todo.StructureTask

interface OnTaskItemClick {
    fun onTaskItemClickFunc(item: StructureTask)
    fun onTaskItemDeleted(item: StructureTask)
    fun onTaskItemClickAlarm(item: StructureTask)
}