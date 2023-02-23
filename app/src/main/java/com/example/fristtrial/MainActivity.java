package com.example.fristtrial;
import static android.text.format.DateUtils.formatElapsedTime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ListView;



import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public Chronometer chrono;
    public Button start, lap, clearLap;
    public ListView lapList;
    public boolean running, tstart;
    public long pauseOffset;
    public int pause1, index = 0;
    public NavigationView navBarView;
    public float lap_mili;
//    public void displayFragment(Fragment fragment) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragView, fragment);
//        fragmentTransaction.commit();
//
//    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chrono = findViewById(R.id.chrTimer);


        ArrayList<String> lapList = new ArrayList<>();
        ListView lapListView = findViewById(R.id.lapTime);
        ArrayAdapter<String> lapAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lapList);
        lapListView.setAdapter(lapAdapter);

        String startBtnText, stopBtnText, lapBtnText, resetBtnText;
        startBtnText = getString(R.string.start);
        stopBtnText = getString(R.string.stop);
        lapBtnText = getString(R.string.lap);
        resetBtnText = getString(R.string.reset);
        start = findViewById(R.id.startBtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (start.getText() == startBtnText) {
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


        lap = findViewById(R.id.lapBtn);
        lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lap.getText() == lapBtnText) {
                    index++;
                    pause1 = (int) (SystemClock.elapsedRealtime() - chrono.getBase());
                    lap_mili = (float)pause1/1000;
                    // will give an index by default
                    lapList.add(index + ": college time:  " + lap_mili);
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

        clearLap = findViewById(R.id.clearLap);
        clearLap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index = 0;
                lapAdapter.clear();
                lapAdapter.notifyDataSetChanged();
            }
        });

    }
}