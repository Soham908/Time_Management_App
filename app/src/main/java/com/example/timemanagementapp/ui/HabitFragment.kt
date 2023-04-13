package com.example.timemanagementapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentHabitBinding


class HabitFragment : Fragment() {

    private lateinit var binding: FragmentHabitBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragment_habit, container, false)
        binding = FragmentHabitBinding.bind(view)


        return view
    }
}