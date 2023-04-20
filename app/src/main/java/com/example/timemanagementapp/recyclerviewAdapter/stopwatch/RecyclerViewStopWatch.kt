package com.example.timemanagementapp.recyclerviewAdapter.stopwatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databaseHandling.interfaces.OnItemClickListenerCustom


class RecyclerViewStopWatch(val onItemClickListenerCustom: OnItemClickListenerCustom,
                            val context: Context, private val list: List<StructureStopWatch>)
    : RecyclerView.Adapter<RecyclerViewStopWatch.StopWatchViewHolder>() {

    inner class StopWatchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val setTime: TextView = itemView.findViewById(R.id.timeRecyclerTime)
        val setWork: TextView = itemView.findViewById(R.id.timeRecyclerLabel)

        fun bind(item: StructureStopWatch) {
            itemView.setOnClickListener{ onItemClickListenerCustom.onItemClickFunc(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopWatchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.time_recycler_item, parent, false)
        return StopWatchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StopWatchViewHolder, position: Int) {
        val item = list[position]
        item.id = position
        // to set position too
        holder.setTime.text = list[position].time
        holder.setWork.text = list[position].work

        holder.bind(item)
    }
}