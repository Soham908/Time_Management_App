package com.example.timemanagementapp.databaseHandling.timeDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TimeViewModel(private val timeDAO: TimeDAO): ViewModel() {



    lateinit var listNow: List<Time>
    val timeList: LiveData<List<Time>> = timeDAO.getTimeInfo()
    fun insertTime(time: Time)
    {
        viewModelScope.launch(Dispatchers.IO) {
            timeDAO.insertTimeDetails(time)

        }

    }

//    suspend fun getthisVar(lis: List<Time>): List<Time>
//    {
//        return withContext(Dispatchers.IO){
//            timeDAO.getTimeInfo()
//        }
//
////        return listNow
//    }


}