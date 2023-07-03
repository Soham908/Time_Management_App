package com.example.timemanagementapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.broadcastReceiver.StopwatchTimeLapReceiver
import com.example.timemanagementapp.structure_data_class.StructureStopWatch
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
        // for passing the current time all over the app
        var num = MutableLiveData<Long>()
        // Just be aware that using global variables can lead to potential issues such as data inconsistency and memory leaks

        // saving the state of the stopwatch when app is closed
        var isPause = false
        var runningSavedState = false
        var isPauseSavedState = isPause

        // for saving the last lap time when the app is closed
        var lastLapTime = 0L

        // to pass the list of lap and the username to the broadcast receiver
        lateinit var lapListBroadcastReceiver: MutableList<StructureStopWatch>
        val usernameBroadcastReceiver = MainActivity.username

        // lap start time save
        var lapStartHourState = 0
        var lapStartMinuteState = 0

        var startTime = SystemClock.elapsedRealtime()
        var elapsedTime = 0L
    }


    override fun onBind(intent: Intent?): IBinder? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        num.postValue(0L)
        notificationBuilder = buildNotification()

        startForeground(NOTIFICATION_ID, buildNotification().build())

        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(1000)
                if (!isPause){
                    elapsedTime = SystemClock.elapsedRealtime() - startTime
                    updateNotification(elapsedTime)
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

        val lapIntent = Intent(this, StopwatchTimeLapReceiver::class.java).apply {
            putExtra("lapTime", "Lap")
        }
        val lapPendingIntent = PendingIntent.getBroadcast(this, 0, lapIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Timer Running")
            .setContentText("00:00:00")
            .setSmallIcon(R.drawable.bottom_nav_homepage)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(0, "Lap", lapPendingIntent)
    }

    private fun updateNotification(elapsedTime: Long) {

        val hours2 = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes2 = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val secs2 = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

        // using a notification builder is better because it does not update all the
        // before the time in the notification kept changing every minute, it did not cause any trouble but still this is much better
        notificationBuilder.setContentTitle("Timer Updated").setContentText(String.format(" %02d:%02d:%02d ", hours2, minutes2, secs2))
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun pauseStopWatch(){
        isPause = true
        elapsedTime = SystemClock.elapsedRealtime() - startTime
        val thatbe = 10
    }
    // 10  25  elt 15,,  40
    fun resumeStopWatch(){
        isPause = false
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        val thisbe = 10
    }


    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        elapsedTime = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}



