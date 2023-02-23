package com.example.fristtrial;

import static android.text.format.DateUtils.formatElapsedTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import java.util.ArrayList;

public class StopWatch_fragment extends Fragment {
    public Chronometer chrono;
    public Button start, lap, clearLap;
    public ListView lapList;
    public boolean running, tstart;
    public long pauseOffset;
    public int pause1, index = 0;
    public float lap_mili;
    public ArrayAdapter<String> lapAdapter;

//    public StopwatchFragment() {
//        // Required empty public constructor
//    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stop_watch_fragment, container, false);
        chrono = view.findViewById(R.id.chrTimerFrag);

        lapList = view.findViewById(R.id.lapTimeFrag);
        ArrayList<String> lapArrayList = new ArrayList<>();
        lapAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lapArrayList);
        lapList.setAdapter(lapAdapter);

        String startBtnText, stopBtnText, lapBtnText, resetBtnText;
        Context context = getActivity();
        startBtnText = context.getString(R.string.start);
        stopBtnText = context.getString(R.string.stop);
        lapBtnText = context.getString(R.string.lap);
        resetBtnText = context.getString(R.string.reset);

        start = view.findViewById(R.id.startBtnFrag);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start.getText().equals(startBtnText)) {
                    start.setText(stopBtnText);
                    chrono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                    chrono.start();
                    running = true;
                    tstart = true;
                    lap.setText(lapBtnText);
                } else {
                    start.setText(startBtnText);
                    chrono.stop();
                    pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
                    running = false;
                    lap.setText(resetBtnText);
                }
            }
        });


        lap = view.findViewById(R.id.lapBtnFrag);
        lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lap.getText().equals(lapBtnText)) {
                    index++;
                    pause1 = (int) (SystemClock.elapsedRealtime() - chrono.getBase());
                    lap_mili = (float) pause1 / 1000;
                    // will give an index by default
                    lapArrayList.add(index + ": college time:  " + lap_mili);
//                    lapList.add("study");
                    lapAdapter.notifyDataSetChanged();
                } else {
                    chrono.setBase(SystemClock.elapsedRealtime());
                    pauseOffset = 0;
                    chrono.stop();
                    // will clear of all the lap time recorded
                    lapAdapter.clear();
                    lapAdapter.notifyDataSetChanged();
                }

            }
        });

        clearLap = view.findViewById(R.id.clearLapFrag);
        clearLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 0;
                lapAdapter.clear();
                lapAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}
