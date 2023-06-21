package com.example.timemanagementapp.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.timemanagementapp.fragments.StopWatchFragment
import com.example.timemanagementapp.services.StopWatchService
import com.example.timemanagementapp.structure_data_class.StructureStopWatch
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StopwatchTimeLapReceiver: BroadcastReceiver() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var date: String

    override fun onReceive(context: Context?, intent: Intent?) {
        val username = StopWatchService.usernameBroadcastReceiver
        val elapsedTime2 = StopWatchService.num.value!!
        val calendar = Calendar.getInstance()
        val hoursCurrent = calendar.get(Calendar.HOUR_OF_DAY)
        val minutesCurrent = calendar.get(Calendar.MINUTE)
        val secondsCurrent = calendar.get(Calendar.SECOND)

        // lap start time from service, so that it works even when app is closed
        var lapStartHour = StopWatchService.lapStartHourState
        var lapStartMinute = StopWatchService.lapStartMinuteState
        if (lapStartHour == 0 && lapStartMinute == 0){
            lapStartHour = StopWatchFragment.lapStartHour
            lapStartMinute = StopWatchFragment.lapStartMinute
        }

        val lastLapTime = StopWatchService.lastLapTime
        val currentElapsedTime = elapsedTime2 - lastLapTime
        val hours2 = TimeUnit.MILLISECONDS.toHours(currentElapsedTime)
        val minutes2 = TimeUnit.MILLISECONDS.toMinutes(currentElapsedTime) % 60
        val seconds2 = TimeUnit.MILLISECONDS.toSeconds(currentElapsedTime) % 60

//        val time =  String.format("CL: %02d:%02d:%02d           TT: %02d:%02d:%02d", hours2, minutes2, seconds2, hoursCurrent, minutesCurrent, secondsCurrent)
        val time =  String.format("%02d:%02d  to  %02d:%02d       Lap  %02d:%02d:%02d", lapStartHour, lapStartMinute, hoursCurrent, minutesCurrent, hours2, minutes2, seconds2)

        date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        firestore = FirebaseFirestore.getInstance()
        val docRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("TimeRecord")
        docRef.update(date, FieldValue.arrayUnion(StructureStopWatch(111, time, "default", "default")))
            .addOnSuccessListener {
                Toast.makeText(context, "added lap", Toast.LENGTH_SHORT).show()
            }
        
        // after lap is pressed update the values to service
        StopWatchService.lastLapTime = elapsedTime2
        StopWatchFragment.lastLapTime = elapsedTime2
        StopWatchFragment.lapStartHour = hoursCurrent
        StopWatchFragment.lapStartMinute = minutesCurrent
        StopWatchService.lapStartHourState = hoursCurrent
        StopWatchService.lapStartMinuteState = minutesCurrent
    }

}