package com.example.timemanagementapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.recyclerviewAdapter.todo.StructureTask
import com.example.timemanagementapp.databinding.FragmentBottomSheetToDoBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class BottomSheetToDo : BottomSheetDialogFragment() {

    lateinit var binding: FragmentBottomSheetToDoBinding
    lateinit var firestore: FirebaseFirestore
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_bottom_sheet_to_do, container, false)
        binding = FragmentBottomSheetToDoBinding.bind(view)

        firestore = FirebaseFirestore.getInstance()
        username = MainActivity.username
        if(arguments != null) {
            val subject = arguments?.getString("subject").toString() ?: ""
            val description = arguments?.getString("description").toString() ?: ""
            binding.todoBottomSub.setText(subject)
            binding.todoBottomDesc.setText(description)

            binding.todoBottomSave.setOnClickListener{
                updateTasks(subject, description)
                dismiss()
            }
        }
        else {
            binding.todoBottomSave.setOnClickListener {
                addTasksOther()
                dismiss()
            }
        }

        return view
    }

    private fun updateTasks(oldSubject: String, oldDescription: String) {
        val taskSubject = binding.todoBottomSub.text.toString()
        val taskDescription = binding.todoBottomDesc.text.toString()
        val oldTask = StructureTask(oldSubject, oldDescription, "", "")
        val task = StructureTask(taskSubject, taskDescription, "", "")
//        val documentRef = firestore.collection("User Details").document("Tasks")
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        documentRef.update("new_task", FieldValue.arrayRemove(oldTask), "new_task", FieldValue.arrayUnion(task))
//        documentRef.update("new task", FieldValue.arrayUnion(task))


        Log.d("dataFirebase1", documentRef.toString())
        Toast.makeText(requireContext(), "Updation done", Toast.LENGTH_SHORT).show()
        val documentRef2 = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")

    }

    private fun addTasksOther(){
        val taskSubject = binding.todoBottomSub.text.toString()
        val taskDescription = binding.todoBottomDesc.text.toString()
        val context = context

        val task = StructureTask(taskSubject, taskDescription, "", "")
//        val documentRef = firestore.collection("User Details").document("Tasks")
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        documentRef.update("new_task", FieldValue.arrayUnion(task))
            .addOnSuccessListener {
                Toast.makeText(context, "task added", Toast.LENGTH_SHORT).show()
                Log.d("dataFirebase1", username)
            }
//        Toast.makeText(requireContext(), "Task Done", Toast.LENGTH_SHORT).show()

    }

}