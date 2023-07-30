package com.example.timemanagementapp.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

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
        binding.loginRedirectSignUp.setOnClickListener{
            val intent = Intent(this, SignUpPage::class.java)
            startActivity(intent)
        }

    }

    private fun verifyUser() {
        val username = binding.loginUserName.editText?.text.toString()
        val password = binding.loginPassword.editText?.text.toString()
        if (username.isNotEmpty() && password.isNotEmpty()) {

            firebaseAuth.signInWithEmailAndPassword("$username@myapp.com", password)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login Done", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    saveUsernamePassword(username, password)
                    finish()
                }
                .addOnFailureListener {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }

        }
        else{
            Toast.makeText(this, "Fill in credentials", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUsernamePassword(username: String, password: String) {
        val sharedPreferences = getSharedPreferences("UserNameLogin", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.apply()

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

}