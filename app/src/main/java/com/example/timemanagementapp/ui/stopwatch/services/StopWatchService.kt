package com.example.timemanagementapp.ui.stopwatch.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.recyclerviewAdapter.stopwatch.StructureStopWatch
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit


class StopWatchService : Service() {

    private var timerJob: Job? = null
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private lateinit var notificationBuilder: NotificationCompat.Builder

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1
        var num = MutableLiveData<Long>()
        // Just be aware that using global variables can lead to potential issues such as data inconsistency and memory leaks
        var isPause = false
        var runningSavedState = false
        var isPauseSavedState = isPause

        var startTime = SystemClock.elapsedRealtime()
        var elapsedTime = 0L
    }


    override fun onBind(intent: Intent?): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        num.postValue(0L)
        notificationBuilder = buildNotification()

        startForeground(NOTIFICATION_ID, buildNotification().build())

        timerJob = CoroutineScope(Dispatchers.Main).launch {
            var seconds = 0L
            while (true) {
                delay(1000)
                if (!isPause){
                    elapsedTime = SystemClock.elapsedRealtime() - startTime
                    seconds++
                    updateNotification(seconds, elapsedTime)
                    num.postValue(elapsedTime)
                }
            }
        }

        return START_NOT_STICKY
    }


    private fun buildNotification(): NotificationCompat.Builder {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Timer Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("Fragment", "stopwatchFragmentIntent")
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Timer Running")
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.bottom_nav_homepage)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    }

    private fun updateNotification(seconds: Long, elapsedTime: Long) {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        //new content
        val hours2 = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes2 = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val secs2 = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

        // using a notification builder is better because it does not update all the
        // before the time in the notification kept changing every minute, it did not cause any trouble but still this is much better
        notificationBuilder.setContentTitle("Timer Updated").setContentText(String.format("%02d:%02d:%02d  %02d:%02d:%02d ", hours, minutes, secs, hours2, minutes2, secs2))
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun pauseStopWatch(){
        isPause = true
        elapsedTime = SystemClock.elapsedRealtime() - startTime
    }
    fun resumeStopWatch(){
        isPause = false
        startTime = SystemClock.elapsedRealtime() - elapsedTime
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        elapsedTime = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}



