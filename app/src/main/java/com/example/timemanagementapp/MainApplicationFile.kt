package com.example.timemanagementapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MainApplicationFile : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set the default theme mode based on the shared preferences
        val isDarkThemeEnabled = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("dark_theme", false)
        if (isDarkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
