package com.example.timemanagementapp.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.timemanagementapp.broadcastReceiver.TaskAlarmBrodcastReciever
import java.util.concurrent.TimeUnit

class TaskAlarmScheduler(val context: Context) {

    private val alarmManager: AlarmManager = context.getSystemService(AlarmManager::class.java)
    
    @SuppressLint("MissingPermission")
    fun scheduleAlarm(time: Long, taskSubject: String){
        val intent = Intent(context, TaskAlarmBrodcastReciever::class.java).apply {
            putExtra("extra_message", taskSubject)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, taskSubject.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // added a use exact alarm permission
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        val timeDifference = time - System.currentTimeMillis()
        val hours = TimeUnit.MILLISECONDS.toHours(timeDifference)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference) % 60
//        val timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        Toast.makeText(context, "Alarm set for $hours and $minutes minutes ", Toast.LENGTH_SHORT).show()

    }
}