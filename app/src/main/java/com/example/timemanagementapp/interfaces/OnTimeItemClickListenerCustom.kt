package com.example.timemanagementapp.interfaces

import com.example.timemanagementapp.structure_data_class.StructureStopWatch


interface OnTimeItemClickListenerCustom {
    fun onItemClickFunc(item: StructureStopWatch)
    fun onTimeItemDelete(item: StructureStopWatch)
}