package com.example.timemanagementapp.broadcastReceiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.services.TaskAlarmScheduler

class TaskAlarmBroadcastReceiver: BroadcastReceiver() {

    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManagerCompat: NotificationManagerCompat
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
        val audioAlarm = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(CHANNEL_ID, "Task Alarm Scheduled", NotificationManager.IMPORTANCE_HIGH).apply {
            setSound(Settings.System.DEFAULT_ALARM_ALERT_URI, audioAlarm)
        }
        notificationManagerCompat.createNotificationChannel(channel)

        Handler(Looper.getMainLooper()).postDelayed({
            notificationManagerCompat.cancel(taskSubject.hashCode())
//            val time = System.currentTimeMillis() + 1 * 60 * 1000
//            TaskAlarmScheduler(context).scheduleAlarm(time, taskSubject)
        }, 60000)

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("Fragment", "taskFragmentIntent")
        }
        val pendingIntent = PendingIntent.getActivity(context, taskSubject.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("task: $taskSubject")
            .setContentText("complete $taskSubject")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.alarm_icon)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

    }
}