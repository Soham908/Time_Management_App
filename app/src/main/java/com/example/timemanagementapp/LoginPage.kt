package com.example.timemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.timemanagementapp.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class LoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginLogin.setOnClickListener{
            verifyUser()
        }

    }

    private fun verifyUser(){
        val username = binding.loginUserName.text.toString()
        val password = binding.loginPassword.text.toString()

        firebaseAuth.signInWithEmailAndPassword("$username@myapp.com", password).
        addOnSuccessListener {
            Toast.makeText(this, "Login Done", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener{
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
        }

    }

}