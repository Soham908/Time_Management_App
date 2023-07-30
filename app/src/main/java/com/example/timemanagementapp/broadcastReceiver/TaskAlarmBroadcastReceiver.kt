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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.structure_data_class.StructureTask
import com.google.firebase.firestore.FirebaseFirestore

class TaskAlarmBroadcastReceiver: BroadcastReceiver() {

    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManagerCompat: NotificationManagerCompat
    private val CHANNEL_ID = "TaskAlarm"
    private lateinit var taskSubject: String
    private lateinit var firestore: FirebaseFirestore
    private lateinit var username: String
    private var taskList = mutableListOf<StructureTask>()
    private lateinit var contextShare: Context

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        contextShare = context
        taskSubject = intent?.getStringExtra("extra_message") ?: return
        username = intent.getStringExtra("username")!!
//        Log.d("dataTime", "broadcast received  $taskSubject")
        notificationManagerCompat = NotificationManagerCompat.from(context)
        notification = buildNotification(context)

        notificationManagerCompat.notify(taskSubject.hashCode(), notification.build())
        firestore = FirebaseFirestore.getInstance()
        deleteAlarmTime()
    }

    @Suppress("UNCHECKED_CAST")
    private fun deleteAlarmTime() {
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        // not good programming practice, will need to change the approach towards this, for now this will work
        documentRef.get()
            .addOnSuccessListener { value ->
                val data = value.get("new_task") as MutableList<Map<*, *>>
                val taskObject = data.map { map ->
                    StructureTask(
                        taskSubject = map["taskSubject"].toString(),
                        taskDescription = map["taskDescription"].toString(),
                        taskTime = map["taskTime"].toString(),
                        taskPriority = map["taskPriority"].toString()
                    )
                }
                taskList.clear()
                for (task in taskObject) {
                    if (task.taskSubject == taskSubject)  task.taskTime = ""
                    taskList.add(task)
                }
                updateAlarmChange()
            }

    }

    private fun updateAlarmChange() {
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        documentRef.update("new_task", taskList)
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