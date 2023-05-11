package com.example.timemanagementapp.databaseHandling.interfaces

import com.example.timemanagementapp.recyclerviewAdapter.stopwatch.StructureStopWatch


interface OnTimeItemClickListenerCustom {
    fun onItemClickFunc(item: StructureStopWatch)
    fun onTimeItemDelete(item: StructureStopWatch)
}