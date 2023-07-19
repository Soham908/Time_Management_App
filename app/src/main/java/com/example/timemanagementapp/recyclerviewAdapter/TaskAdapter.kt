package com.example.timemanagementapp.recyclerviewAdapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanagementapp.R
import com.example.timemanagementapp.interfaces.OnTaskItemClick
import com.example.timemanagementapp.structure_data_class.StructureTask

class TaskAdapter(val onTaskItemClick: OnTaskItemClick, val context: Context, private val list: List<StructureTask>): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){


    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val taskSubject: TextView = itemView.findViewById(R.id.taskRecyclerItemSub)
        val taskDescription: TextView = itemView.findViewById(R.id.taskRecyclerItemDes)
        val taskAlarmTime: TextView = itemView.findViewById(R.id.taskRecyclerAlarmTime)

//        val checkedTextView: CheckedTextView = itemView.findViewById(R.id.taskRecyclerItem3)

        fun bind(item: StructureTask) {
            itemView.setOnClickListener {
                onTaskItemClick.onTaskItemClickFunc(item)
            }
            itemView.findViewById<ImageView>(R.id.taskRecyclerDelete).setOnClickListener {
                onTaskItemClick.onTaskItemDeleted(item)
            }
            itemView.findViewById<ImageView>(R.id.taskRecyclerSetAlarm).setOnClickListener {
                onTaskItemClick.onTaskItemClickAlarm(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.taskSubject.text = list[position].taskSubject
        holder.taskDescription.text = list[position].taskDescription
        holder.taskAlarmTime.text = list[position].taskTime
        val item = list[position]

        holder.bind(item)
    }

}
