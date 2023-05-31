package com.example.timemanagementapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentExerciseBinding


class ExerciseFragment : Fragment() {

    private lateinit var binding: FragmentExerciseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragment_exercise, container, false)
        binding = FragmentExerciseBinding.bind(view)


        return view
    }
}