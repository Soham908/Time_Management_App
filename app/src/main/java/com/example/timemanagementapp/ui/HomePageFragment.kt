package com.example.timemanagementapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databaseHandling.UserDatabase
import com.example.timemanagementapp.databaseHandling.timeDB.Time
import com.example.timemanagementapp.databaseHandling.timeDB.TimeDAO
import com.example.timemanagementapp.databaseHandling.timeDB.TimeViewModel
import com.example.timemanagementapp.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding
    private lateinit var userDatabase: UserDatabase
    private lateinit var timeDAO: TimeDAO
    private lateinit var timeViewModel: TimeViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val view: View = inflater.inflate(R.layout.fragment_home_page, container, false)
        binding = FragmentHomePageBinding.bind(view)

        val show = binding.showDetails
        val remove = binding.removeDetails

        show.setOnClickListener {
            insertData()
        }
//        remove.setOnClickListener { textShow.text = ""}


        return view
    }

    private fun insertData()
    {
        val id = binding.editTextTextPersonName.text.toString()
        val record_time = binding.password.text.toString()
        userDatabase = UserDatabase.getInstance(requireContext())
        timeDAO = userDatabase.timeDao()
        timeViewModel = TimeViewModel(timeDAO)

        timeViewModel.insertTime(Time(id.toInt(), record_time, "", ""))

        Toast.makeText(requireContext(), "Data Inserted", Toast.LENGTH_SHORT).show()
    }


}