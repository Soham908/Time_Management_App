package com.example.timemanagementapp.databaseHandling.todoDB

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ToDoDAO {

    @Query("select * from ToDoList where taskStatus='False'")
    fun getPendingTask(): LiveData<List<ToDo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewTask(todo: ToDo)

    @Query("delete ToDoList where id= :taskId ")
    suspend fun deleteTask(taskId: Int)

    @Update
    suspend fun updateTask(todo: ToDo)

}