package com.example.timemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.timemanagementapp.databinding.ActivityLoginPageBinding
import kotlin.math.log

class LoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


//        val login = findViewById<Button>(R.id.loginLogin)
        binding.loginLogin.setOnClickListener{
            val username = binding.loginUserName.text.toString()
            val password = binding.loginPassword.text.toString()

            if(username == "system" && password == "system")
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Login Done", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
        }

    }
}