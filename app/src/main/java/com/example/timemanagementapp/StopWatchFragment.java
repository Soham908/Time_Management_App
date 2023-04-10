package com.example.timemanagementapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.timemanagementapp.databinding.FragmentStopWatchBinding;

import java.time.chrono.ChronoLocalDateTime;

public class StopWatchFragment extends Fragment {

    public StopWatchFragment()
    {
        // constructor for fragment
    }

    private FragmentStopWatchBinding binding;
    private long timeInSeconds = 0;
    private boolean running = false;
    private Handler handler;
    private Runnable runnable;
    int count = 1;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Example initialization of a button in the fragment's layout
        Button myButton = view.findViewById(R.id.resetButton);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stop_watch, container, false);
        binding = FragmentStopWatchBinding.inflate(inflater, container, false);
        TextView timer = view.findViewById(R.id.timerTextView);
        Button start,stop,reset;
        start = view.findViewById(R.id.startButton);
        stop = view.findViewById(R.id.stopButton);
        reset = view.findViewById(R.id.resetButton);


        // handler and runnable is used to handle all the updates for the UI
        // it always keeps running in the background until it is stopped
        // chronometer also uses handler to update its UI
        // to handle stopwatch and countdown use handler and runnable it acts on the main thread and updates regularly without needed to be called
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long hours = timeInSeconds / 3600;
                long minutes = (timeInSeconds % 3600) / 60;
                long seconds = timeInSeconds % 60;
                @SuppressLint("DefaultLocale") String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                timer.setText(timeString);

                if (running) {
                    timeInSeconds++;
                }
                handler.postDelayed(this, 1000);
            }
        };


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = true;
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                running = false;
                timeInSeconds = 0;
                timer.setText("00:00:00");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

}
