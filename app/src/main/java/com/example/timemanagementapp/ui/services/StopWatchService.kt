package com.example.timemanagementapp.ui.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.recyclerviewAdapter.stopwatch.StructureStopWatch
import kotlinx.coroutines.*


class StopWatchService : Service() {

    private var timerJob: Job? = null
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1
        var num = MutableLiveData<Long>()
        // Just be aware that using global variables can lead to potential issues such as data inconsistency and memory leaks
        var isPause = false
        var runningSavedState = false
        var isPauseSavedState = isPause
        var listSavedState = mutableListOf<StructureStopWatch>()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        num.postValue(0L)
        startForeground(NOTIFICATION_ID, buildNotification())

        timerJob = CoroutineScope(Dispatchers.Main).launch {
            var seconds = 0L
            while (true) {
                delay(1000)
                if (!isPause){
                    seconds++
                    updateNotification(seconds)
                    num.postValue(seconds)
                }
            }
        }

        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Timer Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Timer Running")
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.bottom_nav_homepage)
            .build()
    }

    private fun updateNotification(seconds: Long) {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Timer Running")
            .setContentText(String.format("%02d:%02d:%02d", hours, minutes, secs))
            .setSmallIcon(R.drawable.bottom_nav_homepage)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun pauseStopWatch(){
        isPause = true
    }
    fun resumeStopWatch(){
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}



