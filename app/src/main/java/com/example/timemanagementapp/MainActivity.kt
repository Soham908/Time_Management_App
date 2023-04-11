package com.example.timemanagementapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.timemanagementapp.databaseHandling.UserDatabase
import com.example.timemanagementapp.databaseHandling.UserDatabase.Companion.getInstance
import com.example.timemanagementapp.databaseHandling.timeDB.Time
import com.example.timemanagementapp.databaseHandling.timeDB.TimeDAO
import com.example.timemanagementapp.databaseHandling.timeDB.TimeViewModel
import com.example.timemanagementapp.databinding.ActivityMainBinding
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null
    var userDatabase: UserDatabase? = null
    var timeDAO: TimeDAO? = null
    var timeViewModel: TimeViewModel? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // will automatically bind all the views to its ids during runtime
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        val drawerLayout = binding!!.drawerLayout
        val toolbar = binding!!.materialToolbar
        setSupportActionBar(toolbar)
        // to make this work, change in manifest.xml file the android:theme to no actionbar

        // add navigation icon in xml file to use this
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        val bottomNavigationView = binding!!.bottomNavBar
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.stopwatch -> {
                    Toast.makeText(this@MainActivity, "stopwatch", Toast.LENGTH_SHORT).show()
                    val frag: Fragment = StopWatchFragment()
                    val ft = supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, frag)
                    ft.commit()
                }
                R.id.home -> {
                    Toast.makeText(this@MainActivity, "inserted", Toast.LENGTH_SHORT).show()
                    insertTime()
                }
                R.id.report -> {
                    Toast.makeText(this@MainActivity, "selected", Toast.LENGTH_SHORT).show()
                    displayTime()
                }
            }
            false
        }
        userDatabase = getInstance(this@MainActivity)
        timeDAO = userDatabase!!.timeDao()



    }


    fun insertTime(){
        timeViewModel = TimeViewModel(timeDAO!!)
        var count = 30
        val time: Time = Time(0, "11 $count am", "too much", "application work")
        timeViewModel?.insertTime(time)
        count += 1
    }


    @SuppressLint("SetTextI18n")
    fun displayTime(){
        val tvchange = binding!!.textView

        // you can add the select query in the view model but it requires a lot of unncessary code
        // so just implement it here it is more easier
        userDatabase?.timeDao()?.getTimeInfo()?.observe(this){ timeList ->
            for (element in timeList)
                tvchange.append(element.record_time + " ")

        }
    }


}