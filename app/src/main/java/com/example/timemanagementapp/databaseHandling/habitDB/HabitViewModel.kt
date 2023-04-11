package com.example.timemanagementapp.databaseHandling.habitDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HabitViewModel(private val habitDAO: HabitDAO): ViewModel() {

    val getHabits: LiveData<List<Habit>> = habitDAO.getPendingHabits()

    // insert habits
    fun insertHabit(habits: Habit)
    {
        viewModelScope.launch(Dispatchers.IO)
        {
            habitDAO.insertNewHabits(habits)
        }
    }
}