package com.example.fristtrial;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.example.fristtrial.ui.dashboard.DashboardFragment;
import com.example.fristtrial.ui.home.HomeFragment;
import com.example.fristtrial.ui.notifications.NotificationsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.fristtrial.databinding.ActivityBottmonNavBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;
public class BottmonNav extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public void fragChanger(Fragment frag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainerView, frag);
        ft.commit();

    }

    Fragment fr;

    ActivityBottmonNavBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityBottmonNavBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        MaterialToolbar mtoolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(mtoolbar);
        // this was used to replace the given actionbar with this toolbar
//      // we need to change the theme in manifest file to make this work

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        navView.setOnItemSelectedListener(this::onNavigationItemSelected);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.bottomNavigationView);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        // for opening drawer side bar when button is clicked in the toolbar
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawer.openDrawer(GravityCompat.START);
            }
        });


//        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            // this is used for handling all the bottom nav elements
//            // all the fragments are replaced in the container view according to the button click
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.HomePage:
//                        fragChanger(new DashboardFragment());
//                        break;
//                    case R.id.stopwatch:
//                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                        startActivity(i);
//                        break;
//                    case R.id.report:
//                        fragChanger(new HomeFragmentBottomNav());
//                        break;
//
//
//                }
//                item.setChecked(true);
//
//                return false;
//            }
//
//        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // resource id can change during runtime, so it would be better not to use switch, use if-else,  in the future fix this issue
        switch (item.getItemId()) {
            case R.id.HomePage:
                fragChanger(new DashboardFragment());
                break;
            case R.id.stopwatch:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;
            case R.id.report:
                fragChanger(new HomeFragmentBottomNav());
                break;

        }
        item.setChecked(true);

        return false;
    }
}

