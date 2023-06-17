package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentStopWatchBinding
import com.example.timemanagementapp.interfaces.OnTimeItemClickListenerCustom
import com.example.timemanagementapp.recyclerviewAdapter.StopwatchAdapter
import com.example.timemanagementapp.structure_data_class.StructureStopWatch
import com.example.timemanagementapp.dialogCustom.DialogFragmentStopWatch
import com.example.timemanagementapp.services.StopWatchService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StopWatchFragment : Fragment(), OnTimeItemClickListenerCustom {
    private lateinit var binding: FragmentStopWatchBinding
    private lateinit var service: StopWatchService
    private var running = StopWatchService.runningSavedState
    private var isPause = StopWatchService.isPauseSavedState
    lateinit var adapter: StopwatchAdapter
    companion object {
        var list = mutableListOf<StructureStopWatch>()
        var lastLapTime = 0L
    }
    lateinit var firestore: FirebaseFirestore
    var listenerRegistration: ListenerRegistration? = null
    private lateinit var date: String
    var hours = 0L
    var minutes = 0L
    var secs = 0L

    var elapsedTime2 = 0L
    var currentHour = 0
    var currentMinute = 0
    var currentSecond = 0
    var username = MainActivity.username
    lateinit var start_stop: Button


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
                    requireActivity().startService(Intent(context, StopWatchService::class.java))
                    service.resumeStopWatch()
                    lap.isVisible = true

                    val calendar = Calendar.getInstance()
                    currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                    currentMinute = calendar.get(Calendar.MINUTE)
                    currentSecond = calendar.get(Calendar.SECOND) % 60
                } else if (!running && isPause) {
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
            val nextTimeHour = calendar.get(Calendar.HOUR_OF_DAY)
            val nextTimeMinute = calendar.get(Calendar.MINUTE) % 60
            val nextTimeSecond = calendar.get(Calendar.SECOND) % 60

//            val time =  String.format("%02d:%02d:%02d  to  %02d:%02d%02d       Lap  %02d:%02d:%02d", currentHour, currentMinute, currentSecond, nextTimeHour, nextTimeMinute,nextTimeSecond, hours2, minutes2, secs2)
            val time =  String.format("%02d:%02d  to  %02d:%02d       Lap  %02d:%02d:%02d", currentHour, currentMinute, nextTimeHour, nextTimeMinute, hours2, minutes2, secs2)
            lastLapTime = elapsedTime2
            list.add(StructureStopWatch(null, time, "default", "default"))
            adapter.notifyDataSetChanged()

            currentHour = nextTimeHour
            currentMinute = nextTimeMinute
            currentSecond = nextTimeSecond

            writeDatabaseTest()
            StopWatchService.lapService = list
            StopWatchService.lastLapTime = lastLapTime
        }

        reset.setOnClickListener {
            running = false
            isPause = false
            requireActivity().stopService(Intent(context, StopWatchService::class.java))
            timer.text = getString(R.string.startTime)
            StopWatchService.num.postValue(0L)
            lastLapTime = 0L
            start_stop.text = startString
            lap.isVisible = false
        }

        StopWatchService.num.observe(viewLifecycleOwner) { elapsedTime ->
            elapsedTime2 = elapsedTime
            Log.d("dataFirebase", elapsedTime.toString())
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
        date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        if (username.isEmpty()){
            username = "tester"
        }
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("TimeRecord")
        listenerRegistration = documentRef.addSnapshotListener { value, error ->
            if(error != null){
                Log.e("dataError", "error be :: ${error.message}")
            }
            Log.d("dataFirebase", value?.data.toString() + " " + username)
            val check = value?.get(date) ?: return@addSnapshotListener
            Log.d("dataFirebase", "check var $check")
            if (value.data!!.isNotEmpty()){
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

    fun writeDatabaseTest() {
        date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("TimeRecord")
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
        val context = context
        list.remove(item)
//        firestore.collection("Users_Collection").document(username).collection("More_Details").document("TimeRecord")
//            .update(date, FieldValue.arrayRemove(item))
//            .addOnSuccessListener {
//                Toast.makeText(context, "Time deleted successfull", Toast.LENGTH_SHORT).show()
//            }
        writeDatabaseTest()
        adapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
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
        StopWatchService.lastLapTime = lastLapTime
        StopWatchService.lapService = list
//        writeDatabaseTest()

    }

    override fun onResume() {
        super.onResume()
        running = StopWatchService.runningSavedState
        isPause = StopWatchService.isPauseSavedState
        lastLapTime = StopWatchService.lastLapTime
//        list = StopWatchService.lapService
//        StopWatchService.lapService
        if (running && !isPause) {
            start_stop.text = getString(R.string.stop)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }

}