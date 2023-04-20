package com.example.timemanagementapp.recyclerviewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanagementapp.R


class TimeAdapter(val context: Context, private val listTime: List<TimeRecord>): RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    inner class TimeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var textTitle: TextView = itemView.findViewById(R.id.timeRecyclerTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.time_recycler_item, parent, false)
        return TimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.textTitle.text = listTime[position].label
    }

    override fun getItemCount(): Int {
        return listTime.size
    }




}