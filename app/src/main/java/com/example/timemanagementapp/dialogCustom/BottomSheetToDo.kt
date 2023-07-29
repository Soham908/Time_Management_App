package com.example.timemanagementapp.dialogCustom

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.timemanagementapp.MainActivity
import com.example.timemanagementapp.R
import com.example.timemanagementapp.structure_data_class.StructureTask
import com.example.timemanagementapp.databinding.FragmentBottomSheetToDoBinding
import com.example.timemanagementapp.fragments.TaskFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class BottomSheetToDo(val item: StructureTask) : BottomSheetDialogFragment() {
    constructor() : this(StructureTask("", "", "" ,""))

    lateinit var binding: FragmentBottomSheetToDoBinding
    lateinit var firestore: FirebaseFirestore
    private val taskList = TaskFragment.taskMutableList
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.attributes?.dimAmount = 0f
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
        binding.todoBottomSub.requestFocus()
        Handler(Looper.getMainLooper()).postDelayed({
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.todoBottomSub, InputMethodManager.SHOW_IMPLICIT)
        }, 200)

        return view
    }

    private fun updateTasks(oldSubject: String, oldDescription: String) {
        val taskSubject = binding.todoBottomSub.text.toString()
        val taskDescription = binding.todoBottomDesc.text.toString()
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")

        item.taskSubject = taskSubject
        item.taskDescription = taskDescription
        documentRef.update("new_task", taskList)

        Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
    }

    private fun addTasksOther(){
        val taskSubject = binding.todoBottomSub.text.toString()
        val taskDescription = binding.todoBottomDesc.text.toString()
        val context = context

        val task = StructureTask(taskSubject, taskDescription, "", "")
        val documentRef = firestore.collection("Users_Collection").document(username).collection("More_Details").document("Tasks")
        documentRef.update("new_task", FieldValue.arrayUnion(task))
            .addOnSuccessListener {
                Toast.makeText(context, "task added", Toast.LENGTH_SHORT).show()
            }
    }

}