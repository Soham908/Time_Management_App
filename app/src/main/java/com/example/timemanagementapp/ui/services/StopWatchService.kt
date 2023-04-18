package com.example.timemanagementapp.ui.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.ui.StopWatchFragment
import kotlinx.coroutines.*
import java.util.Timer
import java.util.concurrent.TimeUnit


class StopWatchService : Service() {

    private var timerJob: Job? = null
    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    lateinit var viewModel: StopWatchViewModel

    override fun onCreate() {
        super.onCreate()
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(StopWatchViewModel::class.java)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())

        timerJob = CoroutineScope(Dispatchers.Main).launch {
            var seconds = 0L
            while (true) {
                delay(1000)
                seconds++
                updateNotification(seconds)
                viewModel.getElapsedTime(seconds)
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

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
//        stopForeground(true)
        stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }
}




//class StopWatchService() : Service() {
//    private var elapsedTime: Long = 0
//    private lateinit var handler: Handler
//     lateinit var viewModel: StopWatchViewModel
//
//    override fun onBind(p0: Intent?): IBinder? = null
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotification()
//        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(StopWatchViewModel::class.java)
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        handler = Handler(Looper.getMainLooper())
//
//        Thread {
//            while (true) {
//                elapsedTime += 1000
//                handler.post {
//                    showNotification()
//                    viewModel.getElapsedTime(elapsedTime)
//                }
//                Thread.sleep(1000)
//            }
//        }.start()
//
//        return START_STICKY
//    }
//
//    private fun showNotification() {
//        val notificationIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
//
//        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
//        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
//
//        val notification = Notification.Builder(this, "notiID")
//            .setContentTitle("Stopwatch")
//            .setContentText(String.format("%02d:%02d:%02d", hours, minutes, seconds))
//            .setSmallIcon(R.drawable.bottom_nav_homepage)
//            .setContentIntent(pendingIntent)
//            .setOngoing(true)
//            .build()
//
//        val manager = getSystemService(NotificationManager::class.java)
//        manager.notify(123, notification) // use the same notification ID every time to update the existing notification
//
//        val timer = Timer()
//        timer.scheduleAtFixedRate()
//    }
//
//
//    private fun createNotification() {
//        val showChannel = NotificationChannel("notiID", "Timer Service", NotificationManager.IMPORTANCE_LOW)
//        val manager = getSystemService(NotificationManager::class.java)
//        manager.createNotificationChannel(showChannel)
//    }
//
//
//}
