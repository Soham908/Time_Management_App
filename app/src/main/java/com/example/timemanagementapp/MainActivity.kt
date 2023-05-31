package com.example.timemanagementapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.timemanagementapp.ui.*
import com.example.timemanagementapp.databinding.ActivityMainBinding
import com.example.timemanagementapp.ui.exercise.ExerciseFragment
import com.example.timemanagementapp.ui.habit.HabitFragment
import com.example.timemanagementapp.ui.stopwatch.StopWatchFragment
import com.example.timemanagementapp.ui.todo.TodoListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.firestore.ktx.persistentCacheSettings

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    companion object {
        var username: String = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        authUser()
        super.onCreate(savedInstanceState)
        // will automatically bind all the views to its ids during runtime
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent.getStringExtra("Fragment")
        if (intent != null) {
            openFragment(intent)
        }

        firestore = FirebaseFirestore.getInstance()
        val settings = firestoreSettings { isPersistenceEnabled = true }
        // isPersistenceEnabled is deprecated and needs to be changed

        firestore.firestoreSettings = settings

        materialToolBarFunctions()
        bottomNavBarFunctions()
        sideNavBarFunctions()

    }

    private fun authUser() {
        // made to check if the user is valid else send back to log in page
        val sharedPreferences = getSharedPreferences("UserNameLogin", MODE_PRIVATE)
        username = sharedPreferences.getString("username", "").toString()
        val password = sharedPreferences.getString("password", "")
        if (username.isBlank() && password.isNullOrBlank()){
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }
        // for now i have removed the firebase auth because if the user logs in the app then it must be that the username and pass is correct
        // will add option if user wants to check everytime if the id and pass is correct
        // removed because then everytime when the app opens, internet needs to be conn to check validity
//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseAuth.signInWithEmailAndPassword("$username@myapp.com", "$password")
//            .addOnSuccessListener {
//                Toast.makeText(this, "Login Remember $username $password", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener{
//                val intent = Intent(this, LoginPage::class.java)
//                startActivity(intent)
//            }
    }

    private fun materialToolBarFunctions(){
        val drawerLayout = binding.drawerLayout
        val toolbar = binding.materialToolbar
        setSupportActionBar(toolbar)
        // to make this work, change in manifest.xml file the android:theme to no actionbar

        // add navigation icon in xml file to use this
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
    }

    private fun bottomNavBarFunctions(){
        val bottomNavigationView = binding.bottomNavBar
        lateinit var fragment: Fragment

        bottomNavigationView.setOnItemSelectedListener { item ->
            item.isChecked = true
            when (item.itemId) {
                R.id.bottomNavBarMenuStopwatch  ->      {   fragment = StopWatchFragment()    }
                R.id.bottomNavBarMenuTask       ->      {   fragment = TodoListFragment()     }
                R.id.bottomNavBarMenuHabit      ->      {   fragment = HabitFragment()        }
                R.id.bottomNavBarMenuHome       ->      {   fragment = HomePageFragment()     }
                R.id.bottomNavBarMenuReport     ->      {   fragment = ReportFragment()       }
            }

            fragmentTransaction(fragment)
            false
        }
    }

    private fun sideNavBarFunctions(){
        val sideNavigation = binding.sideNavigationBar
        val tvUsername = sideNavigation.getHeaderView(0).findViewById<TextView>(R.id.sideNavUsername)
        if(username.isNotBlank() && username.isNotEmpty()) {
            tvUsername.text = username
        }
        sideNavigation.setNavigationItemSelectedListener { item ->
            lateinit var fragment: Fragment
            // setting this as a default fragment, the value will change inside of the when statement
            when(item.itemId)
            {
                R.id.todoTab ->             { fragment = TodoListFragment() }
                R.id.habitTrackerTab ->     { fragment = HabitFragment() }
                R.id.exerciseTab ->         { fragment = ExerciseFragment() }
            }

            fragmentTransaction(fragment)

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            false
        }
    }


    private fun fragmentTransaction(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView, fragment).commit()
    }

    // handling tool bar options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_options, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout -> {
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                val sharedPreferences = getSharedPreferences("UserNameLogin", MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                finish()
            }
            R.id.search -> {
                val fragment = SettingsFragment()
                fragmentTransaction(fragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    private fun openFragment(fragmentName: String){
        lateinit var fragment: Fragment
        when(fragmentName){
            "taskFragmentIntent"      ->      { fragment = TodoListFragment()     }
            "stopwatchFragmentIntent" ->      { fragment = StopWatchFragment()    }

        }
        fragmentTransaction(fragment)
    }

}