package com.example.timemanagementapp.ui.todo.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent

import android.content.Context
import android.content.Intent

class TaskAlarmScheduler(val context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
    
    @SuppressLint("MissingPermission")
    fun scheduleAlarm(time: Long, taskSubject: String){
        // time should be in milliseconds
        val intent = Intent(context, TaskAlarmBrodcastReciever::class.java).apply {
            putExtra("extra_message", taskSubject)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, taskSubject.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)

        // added a use exact alarm permission, so that we can set an alarm, without that we cannot set an alarm
        // other wise we need to give another permission in which we have to ask the user, by using this it will automatically grant the permission
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )

    }
}