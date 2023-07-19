package com.example.timemanagementapp.recyclerviewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanagementapp.R
import com.example.timemanagementapp.structure_data_class.StructureHabit

class HabitAdapter(val context: Context, val list: List<StructureHabit>): RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val habitName: TextView = itemView.findViewById(R.id.habitRecyclerName)
        val habitProgress: TextView = itemView.findViewById(R.id.habitRecyclerProgress)
        val habitStreak: TextView = itemView.findViewById(R.id.habitRecyclerStreak)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val item = list[position]
        holder.habitName.text = item.habitName
        holder.habitProgress.text = item.habitProgress
        holder.habitStreak.text = item.habitStreak
    }
}