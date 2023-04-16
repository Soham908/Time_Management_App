package com.example.timemanagementapp.ui

import android.os.Bundle
import android.os.Handler
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentStopWatchBinding

class StopWatchFragment : Fragment() {
    private lateinit var binding: FragmentStopWatchBinding
    private var timeInSeconds: Long = 0
    private var running = false
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val currentTheme = if (isDarkThemeEnabled()) R.style.AppTheme_Dark else R.style.AppTheme_Dark
        val themedInflater = inflater.cloneInContext(ContextThemeWrapper(activity, currentTheme))
        return themedInflater.inflate(R.layout.fragment_stop_watch, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val view = inflater.inflate(R.layout.fragment_stop_watch, container, false)
        binding = FragmentStopWatchBinding.bind(view)

        val timer = binding.timerTextView
        val start = binding.startButton
        val stop = binding.stopButton
        val reset = binding.resetButton


        // handler and runnable is used to handle all the updates for the UI
        // it always keeps running in the background until it is stopped
        // chronometer also uses handler to update its UI
        // to handle stopwatch and countdown use handler and runnable it acts on the main thread and updates regularly without needed to be called
        handler = Handler()
        // handler deprecated change to executor afterwards
        runnable = object : Runnable {
            override fun run() {
                val hours = timeInSeconds / 3600
                val minutes = timeInSeconds % 3600 / 60
                val seconds = timeInSeconds % 60
                val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                timer.text = timeString
                if (running) {
                    timeInSeconds++
                }
                handler.postDelayed(this, 1000)
            }
        }
        // when it starts it takes a little time to start counting
        start.setOnClickListener { running = true }
        stop.setOnClickListener { running = false }
        reset.setOnClickListener {
            running = false
            timeInSeconds = 0
            timer.text = getString(R.string.startTime)
        }

    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 1000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    private fun isDarkThemeEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return sharedPreferences.getBoolean("dark_theme_enabled", false)
    }
}