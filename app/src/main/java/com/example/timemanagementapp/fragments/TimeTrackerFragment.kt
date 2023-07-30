package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentStopWatchBinding
import com.example.timemanagementapp.dialogCustom.DialogFragmentStopWatch
import com.example.timemanagementapp.interfaces.OnTimeItemClickListenerCustom
import com.example.timemanagementapp.recyclerviewAdapter.StopwatchAdapter
import com.example.timemanagementapp.services.StopWatchService
import com.example.timemanagementapp.structure_data_class.StructureStopWatch
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import java.util.concurrent.TimeUnit

class TimeTrackerFragment : Fragment(), OnTimeItemClickListenerCustom {

    private lateinit var binding: FragmentStopWatchBinding
    private lateinit var service: StopWatchService
    private var running = StopWatchService.runningSavedState
    private var isPause = StopWatchService.isPauseSavedState
    lateinit var adapter: StopwatchAdapter
    companion object {
        var list = mutableListOf<StructureStopWatch>()
        var lastLapTime = 0L
        var lapStartHour = 0
        var lapStartMinute = 0
    }
    lateinit var firestore: FirebaseFirestore
    var listenerRegistration: ListenerRegistration? = null
    var hours = 0L
    var minutes = 0L
    var secs = 0L

    var elapsedTime2 = 0L
    var lapStartSeconds = 0
    var username = MainActivity.username
    lateinit var start_stop: Button

    private var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private val currentDate: LocalDate = LocalDate.now()
    private val month = currentDate.month.toString()
    private val currentWeekOfMonth = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfMonth())
    private val year: Int = currentDate.year


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_stop_watch, container, false)
        service = StopWatchService()
        firestore = FirebaseFirestore.getInstance()
        return view
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStopWatchBinding.bind(view)

        val timer = binding.timerTextView
        start_stop = binding.startButton
        val lap = binding.lapButton
        val reset = binding.resetButton
        val startString = getString(R.string.start)
        val stopString = getString(R.string.stop)
        setUpRecyclerView()
        snapshotListener()

        if(running && !isPause){ start_stop.text = stopString; lap.isVisible = true }

        start_stop.setOnClickListener {
            if(start_stop.text == "Start") {
                if (!running && !isPause) {
                    context?.startService(Intent(context, StopWatchService::class.java))
                    service.resumeStopWatch()
                    lap.isVisible = true

                    val calendar = Calendar.getInstance()
                    lapStartHour = calendar.get(Calendar.HOUR_OF_DAY)
                    lapStartMinute = calendar.get(Calendar.MINUTE)
                    lapStartSeconds = calendar.get(Calendar.SECOND)
                }
                else if (!running && isPause) {
                    service.resumeStopWatch()
                    lap.isVisible = true
                    isPause = false
                }
                running = true

                start_stop.text = stopString
            }
            else{
                start_stop.text = startString
                if (running){ service.pauseStopWatch() }
                lap.isVisible = false
                isPause = true
                running = false

            }
        }
        lap.setOnClickListener {

            val elapsedTime = elapsedTime2 - lastLapTime
            val hours2 = TimeUnit.MILLISECONDS.toHours(elapsedTime)
            val minutes2 = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
            val secs2 = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

            val calendar = Calendar.getInstance()
            val lapCurrentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val lapCurrentMinute = calendar.get(Calendar.MINUTE)
            val lapCurrentSeconds = calendar.get(Calendar.SECOND)

            val time =  String.format("%02d:%02d  to  %02d:%02d       Lap  %02d:%02d:%02d", lapStartHour, lapStartMinute, lapCurrentHour, lapCurrentMinute, hours2, minutes2, secs2)
            lastLapTime = 0L
            list.add(StructureStopWatch(null, time, "default", "default"))
            adapter.notifyDataSetChanged()

            lapStartHour = lapCurrentHour
            lapStartMinute = lapCurrentMinute
            lapStartSeconds = lapCurrentSeconds

            writeDatabaseTest()
            StopWatchService.lapListBroadcastReceiver = list
            StopWatchService.lastLapTime = 0L

            StopWatchService.lapStartHourState = lapStartHour
            StopWatchService.lapStartMinuteState = lapStartMinute
            StopWatchService.startTime = SystemClock.elapsedRealtime()
        }

        reset.setOnClickListener {
            running = false
            isPause = false
            StopWatchService.num.postValue(0L)
            lastLapTime = 0L
            start_stop.text = startString
            lap.isVisible = false
            requireActivity().stopService(Intent(context, StopWatchService::class.java))
            timer.text = getString(R.string.startTime)
        }

        StopWatchService.num.observe(viewLifecycleOwner) { elapsedTime ->
            elapsedTime2 = elapsedTime
            hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
            minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
            secs = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60

            timer.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
        }

        DialogFragmentStopWatch.descriptionList.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
            writeDatabaseTest()
        }

    }


    private fun setUpRecyclerView() {
        val recycler = binding.stopwatchRecyclerContainer
        adapter = StopwatchAdapter(this, requireContext(), list)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())
    }



    @Suppress("UNCHECKED_CAST")
    @SuppressLint("NotifyDataSetChanged")
    private fun snapshotListener(){
        if (username.isEmpty()) return

        val documentRef = firestore.document("/Users_Collection/$username/More_Details/TimeRecord/$year/$month/weeks/week${currentWeekOfMonth}")
        listenerRegistration = documentRef.addSnapshotListener { value, error ->
            if(error != null){
                Toast.makeText(context, "error $error ", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            val check = value?.get(date)
            if (check == null){
                list.clear()
                adapter.notifyDataSetChanged()
                documentRef.update(date, listOf<Any>())
                return@addSnapshotListener
            }
            if ( !value.data.isNullOrEmpty() ){
                Log.d("dataFirebase", value.data!!.toString())
                val lapList = value.get(date) as List<Map<*, *>>
                val lapObject = lapList.map { map ->
                    StructureStopWatch(
                        id = map["id"]?.toString()?.toInt(),
                        work = map["work"] as String,
                        description = map["description"] as String?,
                        time = map["time"] as String
                    )
                }
                list.clear()

                for (lap in lapObject) {
                    list.add(lap)
                    Log.d("dataFirebase", "lap example $lap")
                }
                adapter.notifyDataSetChanged()
            }
        }
    }


    private fun writeDatabaseTest() {
        val documentRef = firestore.document("/Users_Collection/$username/More_Details/TimeRecord/$year/$month/weeks/week$currentWeekOfMonth")
//        Log.d("dataFirebase1", "this is address  week$currentWeekOfMonth  $month  $year")
        documentRef.update(date, list)
    }

    override fun onItemClickFunc(item: StructureStopWatch) {
        val customDialog = DialogFragmentStopWatch(item)
        val bundle = Bundle().apply {
            putString("subject", item.work)
        }
        customDialog.arguments = bundle
        customDialog.show(parentFragmentManager, "Dialog Fragment Timer")

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTimeItemDelete(item: StructureStopWatch) {
        list.remove(item)
//        firestore.collection("Users_Collection").document(username).collection("More_Details").document("TimeRecord")
//            .update(date, FieldValue.arrayRemove(item))
//            .addOnSuccessListener {
//                Toast.makeText(context, "Time deleted successful", Toast.LENGTH_SHORT).show()
//            }
        writeDatabaseTest()
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        passVarToService()
//        writeDatabaseTest()

    }

    private fun passVarToService() {
        if (running && !isPause){
            StopWatchService.isPauseSavedState = false
            StopWatchService.runningSavedState = true
        }
        else if (!running && isPause){
            StopWatchService.isPauseSavedState = true
            StopWatchService.runningSavedState = false
        }
        else{
            StopWatchService.isPauseSavedState = false
            StopWatchService.runningSavedState = false
        }
        // pass last lap time
        StopWatchService.lastLapTime = lastLapTime
        // pass list to service for broadcast receiver
        StopWatchService.lapListBroadcastReceiver = list
        // pass the start time of lap
        StopWatchService.lapStartHourState = lapStartHour
        StopWatchService.lapStartMinuteState = lapStartMinute

    }

    override fun onResume() {
        super.onResume()

        if (running && !isPause) {
            start_stop.text = getString(R.string.stop)
        }
        fetchVarFromService()
        checkDate()
    }

    private fun fetchVarFromService() {
        // state of the stopwatch
        running = StopWatchService.runningSavedState
        isPause = StopWatchService.isPauseSavedState
        // last lap time
        lastLapTime = StopWatchService.lastLapTime
        // fetch the lap start time, because when app restarts it will show 0
        lapStartHour = StopWatchService.lapStartHourState
        lapStartMinute = StopWatchService.lapStartMinuteState

    }

    private fun checkDate() {
        val calendar = Calendar.getInstance()
        val timeCheck = calendar.get(Calendar.HOUR_OF_DAY)
        // temp solution
        if (timeCheck > 10){
            viewLifecycleOwner.lifecycleScope.launch {
                date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                snapshotListener()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

}