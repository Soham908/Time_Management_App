package com.example.timemanagementapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.timemanagementapp.databinding.FragmentStopWatchBinding

class StopWatchFragment : Fragment() {
    private var binding: FragmentStopWatchBinding? = null
    private var timeInSeconds: Long = 0
    private var running = false
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    var count = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example initialization of a button in the fragment's layout
        val myButton = view.findViewById<Button>(R.id.resetButton)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stop_watch, container, false)
        binding = FragmentStopWatchBinding.inflate(inflater, container, false)
        val timer = view.findViewById<TextView>(R.id.timerTextView)
        val start: Button
        val stop: Button
        val reset: Button
        start = view.findViewById(R.id.startButton)
        stop = view.findViewById(R.id.stopButton)
        reset = view.findViewById(R.id.resetButton)


        // handler and runnable is used to handle all the updates for the UI
        // it always keeps running in the background until it is stopped
        // chronometer also uses handler to update its UI
        // to handle stopwatch and countdown use handler and runnable it acts on the main thread and updates regularly without needed to be called
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                val hours = timeInSeconds / 3600
                val minutes = timeInSeconds % 3600 / 60
                val seconds = timeInSeconds % 60
                @SuppressLint("DefaultLocale") val timeString =
                    String.format("%02d:%02d:%02d", hours, minutes, seconds)
                timer.text = timeString
                if (running) {
                    timeInSeconds++
                }
                handler!!.postDelayed(this, 1000)
            }
        }
        start.setOnClickListener { running = true }
        stop.setOnClickListener { running = false }
        reset.setOnClickListener {
            running = false
            timeInSeconds = 0
            timer.text = "00:00:00"
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        handler!!.postDelayed(runnable!!, 1000)
    }

    override fun onPause() {
        super.onPause()
        handler!!.removeCallbacks(runnable!!)
    }
}