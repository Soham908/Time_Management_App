package com.example.timemanagementapp.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate

import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.example.timemanagementapp.R

class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_root, rootKey)

        val dark_theme = findPreference<SwitchPreference>("dark_theme")
        dark_theme?.setOnPreferenceChangeListener { _, newValue ->
            if(newValue as Boolean)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            true
        }

    }

    private fun isDarkThemeEnabled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        return sharedPreferences.getBoolean("dark_theme", false)
    }
}
