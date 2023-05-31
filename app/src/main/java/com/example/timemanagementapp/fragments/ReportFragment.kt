package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentReportBinding
import com.google.firebase.firestore.FirebaseFirestore


class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
//    lateinit var adapter: TimeAdapter
//    private var timeList = mutableListOf<TimeRecord>()
    lateinit var firestore: FirebaseFirestore


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
        val recyclerView = binding.timeRecyclerView
        getTimeList()
//        adapter = TimeAdapter(requireContext(),timeList)
//        timeList.add(TimeRecord(54, "this be"))
//        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun getTimeList(){
        firestore = FirebaseFirestore.getInstance()
        val collection = firestore.collection("User Details")
        collection.document("Time_Record").get()
            .addOnSuccessListener {

//                val data = it.toObject(TimeFirebase::class.java)
//                val tasks = data?.Tasks!!.values
//                for(task in tasks){
//                    timeList.add(TimeRecord(data.id, task.toString()))
//                }
//                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener{
                Log.d("dataFirebase1", "Query failed")
            }


        }

}
