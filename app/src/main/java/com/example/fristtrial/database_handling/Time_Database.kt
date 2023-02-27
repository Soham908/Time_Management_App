package com.example.fristtrial.database_handling

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User_Time::class], version = 1)
abstract class Time_Database : RoomDatabase() {
    abstract fun timeDao(): Time_DAO

    companion object {
        private var instance: Time_Database? = null

        fun getInstance(context: Context): Time_Database {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    Time_Database::class.java, "time_Database"
                ).build()
            }
            return instance!!
        }
    }
}