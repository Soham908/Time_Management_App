package com.example.timemanagementapp.ui.stopwatch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentStopWatchBinding
import com.example.timemanagementapp.databaseHandling.interfaces.OnItemClickListenerCustom
import com.example.timemanagementapp.recyclerviewAdapter.stopwatch.RecyclerViewStopWatch
import com.example.timemanagementapp.recyclerviewAdapter.stopwatch.StructureStopWatch
import com.example.timemanagementapp.ui.stopwatch.services.DialogFragmentStopWatch
import com.example.timemanagementapp.ui.stopwatch.services.StopWatchService
import com.google.firebase.firestore.FirebaseFirestore
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class StopWatchFragment : Fragment(), OnItemClickListenerCustom {
    private lateinit var binding: FragmentStopWatchBinding
    private lateinit var service: StopWatchService
    private var running = StopWatchService.runningSavedState
    private var isPause = StopWatchService.isPauseSavedState
    lateinit var adapter: RecyclerViewStopWatch
    var list = mutableListOf<StructureStopWatch>()
    lateinit var firestore: FirebaseFirestore
    var hours = 0L
    var minutes = 0L
    var secs = 0L
    // takes the foreground variable because when the app is closed and opened again at that time the apps var are destroyed
    // and it will start another service because values are reset
    // so when app destroys pass the state of the var in from fragment to service so that state is saved in the service

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentTheme = if (isDarkThemeEnabled()) R.style.AppTheme_Dark else R.style.AppTheme_Dark
        val themedInflater = inflater.cloneInContext(ContextThemeWrapper(activity, currentTheme))
        service = StopWatchService()
        firestore = FirebaseFirestore.getInstance()
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val datebe = Calendar.DATE
        val formattedDate = date.format(Date())
//        date = Locale.getDefault()
        Log.d("elapsedTime", formattedDate)
        Toast.makeText(context, "$formattedDate", Toast.LENGTH_SHORT).show()
        return themedInflater.inflate(R.layout.fragment_stop_watch, container, false)
    }



    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStopWatchBinding.bind(view)

        val timer = binding.timerTextView
        val start_stop = binding.startButton
        val reset_lap = binding.lapButton
        val reset = binding.resetButton
        val startString = getString(R.string.start)
        val stopString = getString(R.string.stop)
        setUpRecyclerView()

        // need to switch the start text when the app is closed and opened again but the service is on
        // when it starts it takes a little time to start counting
        start_stop.setOnClickListener {
            if(start_stop.text == "Start") {
                val thisbe = running
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
            Toast.makeText(context, "$hours $minutes $secs", Toast.LENGTH_SHORT).show()
            val time =  String.format("%02d:%02d:%02d", hours, minutes, secs)
            list.add(StructureStopWatch(null, time, "personal project", null))
            adapter.notifyDataSetChanged()
        }
        reset.setOnClickListener {
            running = false
            isPause = false
            requireActivity().stopService(Intent(context, StopWatchService::class.java))
            timer.text = getString(R.string.startTime)
            start_stop.text = startString
            reset_lap.isVisible = false
        }

        StopWatchService.num.observe(viewLifecycleOwner) { elapsedTime ->
            Log.d("dataFirebase", elapsedTime.toString())
            hours = elapsedTime / 3600
            minutes = (elapsedTime % 3600) / 60
            secs = elapsedTime % 60
            timer.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
        }

        DialogFragmentStopWatch.descriptionList.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onItemClickFunc(item: StructureStopWatch) {
        Toast.makeText(requireContext(), "$item ", Toast.LENGTH_SHORT).show()
        val customDialog = DialogFragmentStopWatch(item)
        customDialog.show(parentFragmentManager, "Dialog Fragment Timer")

    }


    private fun setUpRecyclerView() {
        val recycler = binding.stopwatchRecyclerContainer
        adapter = RecyclerViewStopWatch(this, requireContext(), list)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun isDarkThemeEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return sharedPreferences.getBoolean("dark_theme_enabled", false)
    }

    override fun onPause() {
        super.onPause()
        if (running && isPause){
            StopWatchService.isPauseSavedState = true
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
        snapshotListener()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun snapshotListener(){
        val docRef = firestore.collection("User Details").document("Time Testing")
        docRef.addSnapshotListener { value, _ ->
            Log.d("dataFirebase", value?.data.toString())
            val lapList = value?.get("lap time") as List<Map<*, *>>
            val lapObject = lapList.map { map ->
                StructureStopWatch(
                    id = map["id"].toString().toInt(),
                    work = map["work"] as String,
                    description = map["description"] as String?,
                    time = map["time"] as String
                )
            }
            list.clear()

            for (lap in lapObject) {
                Log.d("dataFirebase", lap.time); list.add(lap)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun writeDatabaseTest() {
        val docRef = firestore.collection("User Details").document("Time Testing")
        docRef.update("lap time", list)
    }



}