package com.example.timemanagementapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentStopWatchBinding
import com.example.timemanagementapp.ui.services.StopWatchService
import com.example.timemanagementapp.ui.services.StopWatchViewModel

class StopWatchFragment : Fragment() {
    private lateinit var binding: FragmentStopWatchBinding
    lateinit var service2: StopWatchService
    private lateinit var service: StopWatchService
    private var running = StopWatchService.runningSavedState
    private var isPause = StopWatchService.isPauseSavedState
    // takes the foreground variable because when the app is closed and opened again at that time the apps var are destroyed
    // and it will start another service because values are reset
    // so when app destroys pass the state of the var in from fragment to service so that state is saved in the service

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentTheme = if (isDarkThemeEnabled()) R.style.AppTheme_Dark else R.style.AppTheme_Dark
        val themedInflater = inflater.cloneInContext(ContextThemeWrapper(activity, currentTheme))
        service = StopWatchService()
        return themedInflater.inflate(R.layout.fragment_stop_watch, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStopWatchBinding.bind(view)

        val timer = binding.timerTextView
        val start = binding.startButton
        val stop = binding.stopButton
        val reset = binding.resetButton


        // when it starts it takes a little time to start counting
        start.setOnClickListener {
            if (!running && !isPause){ requireActivity().startService(Intent(context, StopWatchService::class.java)) }
            else if (!running && isPause){ service.resumeStopWatch() }
            running = true
        }
        stop.setOnClickListener {
            if (running){ service.pauseStopWatch() }
            isPause = true
            running = false
        }
        reset.setOnClickListener {
            running = false
            requireActivity().stopService(Intent(context, StopWatchService::class.java))
            timer.text = getString(R.string.startTime)
        }

        StopWatchService.num.observe(viewLifecycleOwner, Observer {elapsedTime ->
            Log.d("dataFirebase", elapsedTime.toString())
            val hours = elapsedTime / 3600
            val minutes = (elapsedTime % 3600) / 60
            val secs = elapsedTime % 60
            timer.text = String.format("%02d:%02d:%02d", hours, minutes, secs)
        })

    }


    private fun isDarkThemeEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return sharedPreferences.getBoolean("dark_theme_enabled", false)
    }

    override fun onDestroy() {
        super.onDestroy()
        StopWatchService.isPauseSavedState = true
        StopWatchService.runningSavedState = true
    }
}