package com.example.timemanagementapp.interfaces

import com.example.timemanagementapp.structure_data_class.StructureTask

interface OnTaskItemClick {
    fun onTaskItemClickFunc(item: StructureTask)
    fun onTaskItemDeleted(item: StructureTask)
    fun onTaskItemClickAlarm(item: StructureTask)
}