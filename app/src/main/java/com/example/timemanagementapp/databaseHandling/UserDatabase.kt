package com.example.timemanagementapp.databaseHandling

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.timemanagementapp.databaseHandling.timeDB.Time
import com.example.timemanagementapp.databaseHandling.timeDB.TimeDAO

@Database(entities = [Time::class], version = 1,

    )
abstract class UserDatabase : RoomDatabase() {

    abstract fun timeDao(): TimeDAO

    companion object {
        @Volatile
        private var instance: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
           if(instance == null){
               instance = Room.databaseBuilder(
                   context.applicationContext,
                   UserDatabase::class.java,
                   "UserDatabase"
               ).build();
            }
            return instance!!
        }
    }

//    val migration1to2 = object : Migration{
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("")
//        }
//    }
}

