package com.example.timemanagementapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentTodoListBinding


class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodoListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = layoutInflater.inflate(R.layout.fragment_todo_list, container, false)
        binding = FragmentTodoListBinding.bind(view)



        return view
    }
}