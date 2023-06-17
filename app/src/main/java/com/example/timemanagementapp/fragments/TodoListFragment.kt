package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.interfaces.OnTaskItemClick
import com.example.timemanagementapp.structure_data_class.StructureTask
import com.example.timemanagementapp.databinding.FragmentTodoListBinding
import com.example.timemanagementapp.recyclerviewAdapter.TaskAdapter
import com.example.timemanagementapp.dialogCustom.BottomSheetToDo
import com.example.timemanagementapp.services.TaskAlarmScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class TodoListFragment : Fragment(), OnTaskItemClick {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var adapter: TaskAdapter
    private var list = mutableListOf<StructureTask>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var parentFragment: FragmentManager
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragment_todo_list, container, false)
        binding = FragmentTodoListBinding.bind(view)
        parentFragment = parentFragmentManager
        firestore = FirebaseFirestore.getInstance()
        setRecyclerView()
        username = MainActivity.username
        binding.taskAddTask.setOnClickListener{

            BottomSheetToDo().show(childFragmentManager, "this is bottom sheet frag")
        }

        val firebaseAuth = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("dataFirebase1", firebaseAuth.toString() + " todo on create")

        selectData()
        return view
    }

    private fun setRecyclerView(){
        adapter = TaskAdapter(this, requireContext(), list)
        val recyclerview = binding.taskRecyclerView
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
    }


    @SuppressLint("NotifyDataSetChanged")
    fun selectData(){
    // /Users_Collection/Soham/More Details/Tasks
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")

        documentRef.addSnapshotListener{ value, error ->
            if(error != null){
                Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
            }
            val data = value?.data?.values ?: return@addSnapshotListener
            Log.d("dataFirebase", data.toString())
            val thisbe = value.get("new_task")

            if(thisbe != null) {
                val taskList = thisbe as List<Map<*, *>>

                val taskObject = taskList.map { map ->
                    StructureTask(
                        taskSubject = map["taskSubject"].toString(),
                        taskDescription = map["taskDescription"].toString(),
                        taskTime = map["taskTime"].toString(),
                        taskPriority = map["taskPriority"].toString()
                    )
                }
                Log.d("dataFirebase1", taskObject.toString())
                list.clear()
                for (task in taskObject) {
                    list.add(task)
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
        val bottomSheet = BottomSheetToDo()
        bottomSheet.arguments = bundle
        bottomSheet.show(parentFragmentManager, "ths be")
    }

    override fun onTaskItemDeleted(item: StructureTask) {
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        documentRef.update("new_task", FieldValue.arrayRemove(item))
    }

    override fun onTaskItemClickAlarm(item: StructureTask) {

        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val minutes = calender.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, hour2, minute2 ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour2)
            calendar.set(Calendar.MINUTE, minute2)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val alarmTime = calendar.timeInMillis

            Log.d("dataTime", "todo  $minute2  $hour2  ${item.taskSubject}  $item")
            TaskAlarmScheduler(requireContext()).scheduleAlarm(alarmTime, item.taskSubject)
        }, hour, minutes, false)

        timePickerDialog.show()
    }


}