package com.example.timemanagementapp.broadcastReceiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R

class TaskAlarmBrodcastReciever: BroadcastReceiver() {

    private lateinit var notification: NotificationCompat.Builder
    lateinit var notificationManagerCompat: NotificationManagerCompat
    private val CHANNEL_ID = "TaskAlarm"
    private lateinit var taskSubject: String


    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        taskSubject = intent?.getStringExtra("extra_message") ?: return
        Log.d("dataTime", "broadcast received  $taskSubject")
        notificationManagerCompat = NotificationManagerCompat.from(context)
        notification = buildNotification(context)

        notificationManagerCompat.notify(taskSubject.hashCode(), notification.build())
    }

    private fun buildNotification(context: Context): NotificationCompat.Builder {
        val channel = NotificationChannel(CHANNEL_ID, "Task Alarm Scheduled", NotificationManager.IMPORTANCE_HIGH)
        notificationManagerCompat.createNotificationChannel(channel)

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("Fragment", "taskFragmentIntent")
        }
        val pendingIntent = PendingIntent.getActivity(context, taskSubject.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("task: $taskSubject")
            .setContentText("complete $taskSubject")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.alarm_icon)

    }
}