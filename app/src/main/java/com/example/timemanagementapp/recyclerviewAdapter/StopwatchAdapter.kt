package com.example.timemanagementapp.recyclerviewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemanagementapp.R
import com.example.timemanagementapp.interfaces.OnTimeItemClickListenerCustom
import com.example.timemanagementapp.structure_data_class.StructureStopWatch


class StopwatchAdapter(val onTimeItemClickListenerCustom: OnTimeItemClickListenerCustom,
                       val context: Context, private val list: List<StructureStopWatch>)
    : RecyclerView.Adapter<StopwatchAdapter.StopWatchViewHolder>() {

    inner class StopWatchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val setTime: TextView = itemView.findViewById(R.id.timeRecyclerTime)
        val setWork: TextView = itemView.findViewById(R.id.timeRecyclerLabel)

        fun bind(item: StructureStopWatch) {
            itemView.setOnClickListener{ onTimeItemClickListenerCustom.onItemClickFunc(item) }
            itemView.findViewById<ImageView>(R.id.timeRecyclerDelete).setOnClickListener {
                onTimeItemClickListenerCustom.onTimeItemDelete(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopWatchViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_item_time, parent, false)
        return StopWatchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StopWatchViewHolder, position: Int) {
        val item = list[list.size - position - 1]
        item.id = position
        // to set the position
        holder.setTime.text = item.time
        holder.setWork.text = item.work

        holder.bind(item)
    }
}