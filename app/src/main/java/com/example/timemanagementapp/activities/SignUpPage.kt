package com.example.timemanagementapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.timemanagementapp.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpPage : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpPageBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.signupSignup.setOnClickListener{
            signUpUser()
        }
        binding.signupRedirectLogin.setOnClickListener{
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }

    }

    private fun signUpUser(){
        username = binding.signupUserName.text.toString()
        val password = binding.signupPassword.text.toString()

        firebaseAuth.createUserWithEmailAndPassword("$username@myapp.com", password).
        addOnCompleteListener(this) {
            if(it.isSuccessful){
                Toast.makeText(this, "SignUp success", Toast.LENGTH_SHORT).show()
                createUserSchema()
            }
            else
                Toast.makeText(this, "SignUp Not Done", Toast.LENGTH_SHORT).show()
        }

    }

    private fun createUserSchema() {
        val mainRef = firestore.collection("Users_Collection").document(username).collection("More_Details")
        val batch = firestore.batch()
        val nameSave = firestore.collection("Users_Collection").document(username)
        batch.set(nameSave, mapOf("name" to username))
        val tasks = mainRef.document("Tasks")
        batch.set(tasks, {})
        val habits = mainRef.document("Habits")
        batch.set(habits, {})
        val timeRecord = mainRef.document("TimeRecord")
        batch.set(timeRecord, {})
        val exercises = mainRef.document("Exercises")
        batch.set(exercises, {})
        // reports, achievements, goals, notes
        val reports = mainRef.document("Reports")
        batch.set(reports, {})
        val goals = mainRef.document("Goals")
        batch.set(goals, {})
        val achievements = mainRef.document("Achievements")
        batch.set(achievements, {})
        val notes = mainRef.document("Notes")
        batch.set(notes, {})

        batch.commit()

    }


}