package com.example.fristtrial.database_handling

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TimeDB_Implementation {

    //    val database : Database
    private lateinit var database: Time_Database
    private lateinit var userDao: Time_DAO

    fun init(database: Time_Database, userDao: Time_DAO)
    {
        this.database = database
        this.userDao = userDao
    }

    fun insertDataThis(first_J: String, last_J: String)
    {
//        val first: String = first_J
//        val last: String = last_J
        val user = User_Time(first_J, last_J)
        GlobalScope.launch {
            userDao.insert(user)
        }
    }
    fun deleteAllUser()
    {
        GlobalScope.launch {
            userDao.deleteAllUsers()
        }
    }
    fun delete()
    {

    }
    fun update()
    {

    }

    suspend fun returnData(): LiveData<List<User_Time>> {
        return userDao.getAllUsers()
    }



}

// you could use this when you do not want to use 2 functions
// or could also use livedata in java itself so that dont have to use all this
//suspend fun getAllUsersKT(callback: (List<User>) -> Unit) {
//    withContext(Dispatchers.IO) {
//        val allUsers = userDao.getAllUsers()
//        callback(allUsers)
//    }
//}
