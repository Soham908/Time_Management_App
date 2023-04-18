package com.example.timemanagementapp.ui.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.timemanagementapp.ui.StopWatchFragment


class StopWatchViewModel(): ViewModel() {
    val _elapsedTime = MutableLiveData<Long>(0)
    val elapsedTime: LiveData<Long> get() = _elapsedTime

    fun getElapsedTime(time: Long) {
        _elapsedTime.postValue(time)
    }
}