package com.example.timemanagementapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.timemanagementapp.databinding.ActivitySignUpPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class SignUpPage : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpPageBinding
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.signupSignup.setOnClickListener{
            signUpUser()
        }

    }

    private fun signUpUser(){
        val username = binding.signupUserName.text.toString()
        val password = binding.signupPassword.text.toString()

        firebaseAuth.createUserWithEmailAndPassword("$username@myapp.com", password).
        addOnCompleteListener(this) {
            if(it.isSuccessful){
                Toast.makeText(this, "SignUp success", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this, "SignUp Not Done", Toast.LENGTH_SHORT).show()
        }

    }


}