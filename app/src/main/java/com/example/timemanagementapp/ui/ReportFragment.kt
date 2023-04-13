package com.example.timemanagementapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databaseHandling.UserDatabase
import com.example.timemanagementapp.databaseHandling.timeDB.Time
import com.example.timemanagementapp.databaseHandling.timeDB.TimeDAO
import com.example.timemanagementapp.databinding.FragmentReportBinding
import com.example.timemanagementapp.recyclerviewAdapter.TimeAdapter
import com.example.timemanagementapp.recyclerviewAdapter.TimeRecord


class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    lateinit var adapter: TimeAdapter
    private var timeList = mutableListOf<TimeRecord>()
    private lateinit var userDatabase: UserDatabase
    private lateinit var timeDAO: TimeDAO


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_report, container, false)
        binding = FragmentReportBinding.bind(view)

        val click = binding.button
        click.setOnClickListener{ getTimeList() }
        recyclerView()

        return view
    }

    private fun recyclerView()
    {
        val recylerView = binding.timeRecyclerView
        getTimeList()
        adapter = TimeAdapter(requireContext(),timeList)
        timeList.add(TimeRecord(54, "this be"))
        recylerView.adapter = adapter
        recylerView.layoutManager = LinearLayoutManager(context)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getTimeList(){
        userDatabase = UserDatabase.getInstance(requireContext())
        timeDAO = userDatabase.timeDao()

        // you can add the select query in the view model but it requires a lot of unnecessary code
        // so just implement it here it is more easier
        userDatabase.timeDao().getTimeInfo().observe(viewLifecycleOwner){ timeList2 ->
            for (element in timeList2)
            {
                timeList.add(TimeRecord(element.id, element.record_time))
            }
            adapter.notifyDataSetChanged()

        }
    }
}