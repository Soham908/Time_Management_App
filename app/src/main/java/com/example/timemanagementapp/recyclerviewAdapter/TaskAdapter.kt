package com.example.timemanagementapp.recyclerviewAdapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databaseHandling.TaskFirebase

class TaskAdapter(val context: Context, private val list: List<TaskFirebase>): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val taskSubject: TextView = itemView.findViewById(R.id.taskRecyclerItem2)
        val taskDescription: TextView = itemView.findViewById(R.id.taskRecyclerItem)
        val checkedTextView: CheckedTextView = itemView.findViewById(R.id.taskRecyclerItem3)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.task_recycler_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskSubject.text = list[position].taskSubject
        holder.taskDescription.text = list[position].taskDescription

    }
}
