package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
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
    var list = mutableListOf<StructureStopWatch>()
    lateinit var firestore: FirebaseFirestore
    lateinit var listenerRegistration: ListenerRegistration
    private lateinit var date: String
    var hours = 0L
    var minutes = 0L
    var secs = 0L
    var lastLapTime = 0L
    var elapsedTime2 = 0L
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
        val reset_lap = binding.lapButton
        val reset = binding.resetButton
        val startString = getString(R.string.start)
        val stopString = getString(R.string.stop)
        setUpRecyclerView()
        snapshotListener()

        if(running && !isPause){ start_stop.text = stopString; reset_lap.isVisible = true }

        start_stop.setOnClickListener {
            if(start_stop.text == "Start") {
                if (!running && !isPause) {
                    requireActivity().startService(Intent(context, StopWatchService::class.java))
                    service.resumeStopWatch()
                    reset_lap.isVisible = true
                } else if (!running && isPause) {
                    service.resumeStopWatch()
                    reset_lap.isVisible = true
                }
                running = true

                start_stop.text = stopString
            }
            else{
                start_stop.text = startString
                if (running){ service.pauseStopWatch() }
                reset_lap.isVisible = false
                isPause = true
                running = false
            }
        }
        reset_lap.setOnClickListener {

            val elapsedTime = elapsedTime2 - lastLapTime
            val hours2 = TimeUnit.MILLISECONDS.toHours(elapsedTime)
            val minutes2 = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
            val secs2 = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
            val time =  String.format("CL: %02d:%02d:%02d           TT: %02d:%02d:%02d", hours2, minutes2, secs2, hours, minutes, secs,)
            lastLapTime = elapsedTime2
            list.add(StructureStopWatch(null, time, "personal project", "default"))
            adapter.notifyDataSetChanged()
        }

        reset.setOnClickListener {
            running = false
            isPause = false
            requireActivity().stopService(Intent(context, StopWatchService::class.java))
            timer.text = getString(R.string.startTime)
            StopWatchService.num.postValue(0L)
            start_stop.text = startString
            reset_lap.isVisible = false
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
        }

    }


    private fun setUpRecyclerView() {
        val recycler = binding.stopwatchRecyclerContainer
        adapter = StopwatchAdapter(this, requireContext(), list)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun isDarkThemeEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return sharedPreferences.getBoolean("dark_theme_enabled", false)
    }



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
            Log.d("dataFirebase", check.toString())
            if (value.data!!.isNotEmpty()){
                Log.d("dataFirebase", value.data!!.toString())
                val lapList = value.get(date) as List<Map<*, *>>
                val lapObject = lapList.map { map ->
                    StructureStopWatch(
                        id = map["id"].toString().toInt(),
                        work = map["work"] as String,
                        description = map["description"] as String?,
                        time = map["time"] as String
                    )
                }
                list.clear()

//                for (lap in lapObject) {
//                    Log.d("dataFirebase", lap.time); list.add(lap)
//                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun writeDatabaseTest() {
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
        firestore.collection("Users_Collection").document(username).collection("More_Details").document("TimeRecord")
            .update(date, FieldValue.arrayRemove(item))
            .addOnSuccessListener {
                Toast.makeText(context, "Time deleted", Toast.LENGTH_SHORT).show()
            }
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
        writeDatabaseTest()

    }

    override fun onResume() {
        super.onResume()
        running = StopWatchService.runningSavedState
        isPause = StopWatchService.isPauseSavedState
        if (running && !isPause) {
            start_stop.text = getString(R.string.stop)
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    override fun onDestroy() {
        super.onDestroy()
        if (listenerRegistration != null) {
            listenerRegistration.remove()
        }
    }

}