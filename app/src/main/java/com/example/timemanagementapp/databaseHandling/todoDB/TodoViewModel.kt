package com.example.timemanagementapp.databaseHandling.todoDB

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TodoViewModel(private val todoDao: ToDoDAO): ViewModel() {

    // to show livedata in activity
    val showPending: LiveData<List<ToDo>> = todoDao.getPendingTask()

    // to insert task
    fun insertTask(todolist: ToDo)
    {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.insertNewTask(todolist)
        }
    }
}