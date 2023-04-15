package com.example.timemanagementapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databaseHandling.TaskFirebase
import com.example.timemanagementapp.databinding.FragmentTodoListBinding
import com.example.timemanagementapp.recyclerviewAdapter.TaskAdapter
import com.google.firebase.firestore.FirebaseFirestore


class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding
    private lateinit var adapter: TaskAdapter
    private var list = mutableListOf<TaskFirebase>()
    private lateinit var firestore: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragment_todo_list, container, false)
        binding = FragmentTodoListBinding.bind(view)

        firestore = FirebaseFirestore.getInstance()
        setRecyclerView()
        binding.taskAddTask.setOnClickListener{
//            addTasks()
            BottomSheetToDo().show(childFragmentManager, "this is bottom sheet frag")
        }
        binding.showData.setOnClickListener{
            selectData()
        }


        return view
    }

    private fun setRecyclerView(){
        adapter = TaskAdapter(requireContext(), list)
        val recyclerview = binding.taskRecyclerView
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addTasks(){
        val taskSubject = binding.taskSubject.text.toString()
        val taskDescription = binding.taskDescription.text.toString()
        val taskTime = binding.taskTime.text.toString()
        val taskPriority = binding.taskPriority.text.toString()

        val task = TaskFirebase(taskSubject, taskDescription, taskTime, taskPriority)
        val documentRef = firestore.collection("User Details").document("Tasks")
        documentRef.update(taskSubject, task)
        Toast.makeText(requireContext(), "Task Done", Toast.LENGTH_SHORT).show()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun selectData(){
        val documentRef = firestore.collection("User Details").document("Tasks")
        documentRef.get().addOnSuccessListener {
            val data = it.data!! as Map<*, *>
            list.clear()
            val keys = it.data!!.keys
            for(key in keys){
                getData(data, key)
            }

        }.addOnFailureListener { exception ->
            Toast.makeText(requireContext(), "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData(data: Map<*, *>, key: String)
    {
        val datafunc = data[key] as Map<*, *>
        val taskSubject = datafunc["taskSubject"].toString()
        val taskDescription = datafunc["taskDescription"].toString()
        val taskTime = datafunc["taskTime"].toString()
        val taskPriority = datafunc["taskPriority"].toString()
        Log.d("dataFirebase1", taskPriority)
        list.add(TaskFirebase(taskSubject, taskDescription, taskTime, taskPriority))
        adapter.notifyDataSetChanged()
    }


}