package com.example.timemanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.timemanagementapp.databaseHandling.UserDatabase;
import com.example.timemanagementapp.databaseHandling.timeDB.Time;
import com.example.timemanagementapp.databaseHandling.timeDB.TimeDAO;
import com.example.timemanagementapp.databaseHandling.timeDB.TimeViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.timemanagementapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

import kotlinx.coroutines.Dispatchers;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    UserDatabase userDatabase;
    TimeDAO timeDAO;
    TimeViewModel timeViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // will automatically bind all the views to its ids during runtime
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawerLayout = binding.drawerLayout;
        MaterialToolbar toolbar = binding.materialToolbar;
        setSupportActionBar(toolbar);
        // to make this work, change in manifest.xml file the android:theme to no actionbar

        // add navigation icon in xml file to use this
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { drawerLayout.openDrawer(GravityCompat.START); }
        });

        BottomNavigationView bottomNavigationView = binding.bottomNavBar;
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.stopwatch:
                        Toast.makeText(MainActivity.this, "stopwatch", Toast.LENGTH_SHORT).show();
                        Fragment frag = new StopWatchFragment();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, frag);
                        ft.commit();
                        break;
                    case R.id.home:
                        Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                        insertTime();
                        break;
                    case R.id.report:
                        Toast.makeText(MainActivity.this, "report", Toast.LENGTH_SHORT).show();
                        displayTime();
                        break;
                }
                return false;
            }
        });

        userDatabase = UserDatabase.Companion.getInstance(MainActivity.this);
        timeDAO = userDatabase.timeDao();

    }

    public void insertTime()
    {

        timeViewModel = new TimeViewModel(timeDAO);
        Time time1 = new Time(0, "too much time wasted", "struggling ", "database");
        timeViewModel.insertTime(time1);
    }

    public void displayTime()
    {
//        timeViewModel = new ViewModelProvider(this).get(TimeViewModel.class);

        timeViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new TimeViewModel(timeDAO);
            }
        }).get(TimeViewModel.class);

        timeViewModel.getTimeList().observe(this, new Observer<List<Time>>() {
            @Override
            public void onChanged(List<Time> times) {
                // do something with the list of times
                TextView setTv = binding.textView;
                int count = times.size();
                for (int i = 0; i < count; i++)
                {
                    setTv.setText(times.get(i).getRecord_time());
                }
            }
        });
    }

//    private void displayAllUsers() {
//        userDao.getAllUsers().observe(owner, new Observer<List<User>>() {
//            @Override
//            public void onChanged(List<User> users) {
//                String thisbeFirst;
//                String thisbeLast;
//                forStringAdapt.clear();
//                int count = users.size();
//                for (int i = 0; i < count; i++) {
//                    thisbeFirst = users.get(i).getFirstName();
//                    thisbeLast = users.get(i).getLastName();
//                    forStringList.add("First name:" + thisbeFirst + " Last Name:" + thisbeLast);
//                    Log.d("Thisbe", " display this for chekcing " + thisbeFirst);
//                }
//
//
//                forStringAdapt.notifyDataSetChanged();
//                userDao.getAllUsers().removeObserver(this);
//
//            }
//        });
}