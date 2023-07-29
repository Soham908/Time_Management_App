package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentTaskListBinding
import com.example.timemanagementapp.interfaces.OnTaskItemClick
import com.example.timemanagementapp.structure_data_class.StructureTask
import com.example.timemanagementapp.recyclerviewAdapter.TaskAdapter
import com.example.timemanagementapp.dialogCustom.BottomSheetToDo
import com.example.timemanagementapp.services.TaskAlarmScheduler
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


class TaskFragment : Fragment(), OnTaskItemClick {

    private lateinit var binding: FragmentTaskListBinding
    private lateinit var adapter: TaskAdapter
    companion object {
        var taskMutableList = mutableListOf<StructureTask>()
    }
    private lateinit var firestore: FirebaseFirestore
    private lateinit var parentFragment: FragmentManager
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragment_task_list, container, false)
        binding = FragmentTaskListBinding.bind(view)
        parentFragment = parentFragmentManager
        firestore = FirebaseFirestore.getInstance()
        setRecyclerView()
        username = MainActivity.username
        binding.taskAddTask.setOnClickListener{

            BottomSheetToDo().show(childFragmentManager, "this is bottom sheet frag")
        }

        selectData()
        return view
    }

    private fun setRecyclerView(){
        adapter = TaskAdapter(this, requireContext(), taskMutableList)
        val recyclerview = binding.taskRecyclerView
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
    }


    @Suppress("UNCHECKED_CAST")
    @SuppressLint("NotifyDataSetChanged")
    fun selectData(){
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")

        documentRef.addSnapshotListener{ value, error ->
            if(error != null){
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
            value?.data?.values ?: return@addSnapshotListener
//            Log.d("dataFirebase", data.toString())
            val data = value.get("new_task")

            if(data != null) {
                val taskList = data as List<Map<*, *>>

                val taskObject = taskList.map { map ->
                    StructureTask(
                        taskSubject = map["taskSubject"].toString(),
                        taskDescription = map["taskDescription"].toString(),
                        taskTime = map["taskTime"].toString(),
                        taskPriority = map["taskPriority"].toString()
                    )
                }
                taskMutableList.clear()
                for (task in taskObject) {
                    taskMutableList.add(task)
                }
                adapter.notifyDataSetChanged()
            }
        }

    }


    override fun onTaskItemClickFunc(item: StructureTask) {
        val bundle = Bundle().apply {
            putString("subject", item.taskSubject)
            putString("description", item.taskDescription)
        }
        val bottomSheet = BottomSheetToDo(item)
        bottomSheet.arguments = bundle
        bottomSheet.show(parentFragmentManager, "ths be")
    }

    override fun onTaskItemDeleted(item: StructureTask) {
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        documentRef.update("new_task", FieldValue.arrayRemove(item))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onTaskItemClickAlarm(item: StructureTask) {

        val calender = Calendar.getInstance()
        val hours = calender.get(Calendar.HOUR_OF_DAY)
        val minutes = calender.get(Calendar.MINUTE)

        val materialTimePicker = MaterialTimePicker
            .Builder()
            .setTitleText("Select a time")
            .setHour(hours)
            .setMinute(minutes)
            .setTheme(R.style.CustomTimePicker)
            .build()

        materialTimePicker.apply {
            addOnPositiveButtonClickListener {
                val selectedHour = materialTimePicker.hour
                val selectedMinute = materialTimePicker.minute

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                val alarmTime = calendar.timeInMillis
                val selectedTime = LocalTime.of(selectedHour, selectedMinute)
                val formattedTime = formatTime(selectedTime)

                TaskAlarmScheduler(requireContext()).scheduleAlarm(alarmTime, item.taskSubject)
                item.taskTime = "Alarm: $formattedTime"
                adapter.notifyDataSetChanged()
                updateTask()
            }
        }

        materialTimePicker.show(childFragmentManager, "alarm")
    }

    private fun updateTask() {
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        documentRef.update("new_task", taskMutableList)
    }

    private fun formatTime(time: LocalTime): String {
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return time.format(formatter)
    }

}