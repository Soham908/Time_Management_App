package com.example.timemanagementapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.timemanagementapp.R
import com.example.timemanagementapp.recyclerviewAdapter.todo.TaskFirebase
import com.example.timemanagementapp.databinding.FragmentBottomSheetToDoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore


class BottomSheetToDo : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetToDoBinding
    lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_bottom_sheet_to_do, container, false)
        binding = FragmentBottomSheetToDoBinding.bind(view)

        firestore = FirebaseFirestore.getInstance()
        binding.todoBottomSave.setOnClickListener{

            addTasksOther()
            dismiss()
        }

        return view
    }

    private fun addTasksOther(){
        val taskSubject = binding.todoBottomSub.text.toString()
        val taskDescription = binding.todoBottomDesc.text.toString()

        val task = TaskFirebase(taskSubject, taskDescription, "", "")
        val documentRef = firestore.collection("User Details").document("Tasks")
        documentRef.update(taskSubject, task)
        Toast.makeText(requireContext(), "Task Done", Toast.LENGTH_SHORT).show()

    }

}