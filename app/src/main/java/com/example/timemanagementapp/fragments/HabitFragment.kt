package com.example.timemanagementapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentHabitBinding
import com.example.timemanagementapp.recyclerviewAdapter.HabitAdapter
import com.example.timemanagementapp.structure_data_class.StructureHabit
import com.example.timemanagementapp.structure_data_class.StructureStopWatch


class HabitFragment : Fragment() {

    private lateinit var binding: FragmentHabitBinding
    private var listHabit = mutableListOf<StructureHabit>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragment_habit, container, false)
        binding = FragmentHabitBinding.bind(view)

        listHabit.add(StructureHabit(0, "Play Guitar", "4/5", "2 days"))
        listHabit.add(StructureHabit(1, "Read Book", "2/5", "5 days"))
        listHabit.add(StructureHabit(2, "Stretching", "3/5", "7 days"))
        listHabit.add(StructureHabit(3, "Make Sandwich", "4/5", "1 day"))
        listHabit.add(StructureHabit(4, "Clean Room", "1/5", "8 days"))


        setUpRecyclerView()
        return view
    }

    private fun setUpRecyclerView() {
        val adapter = HabitAdapter(requireContext(), listHabit)
        val habitRecyclerView = binding.habitRecyclerView
        habitRecyclerView.adapter = adapter
        habitRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}