package com.example.timemanagementapp.UI

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {

    private lateinit var binding: FragmentHomePageBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        val view: View = inflater.inflate(R.layout.fragment_home_page, container, false)
        binding = FragmentHomePageBinding.bind(view)

        val show = binding.showDetails
        val remove = binding.removeDetails
        val textShow = binding.textView3

        show.setOnClickListener {
            val name = binding.editTextTextPersonName.text.toString()
            val pass = binding.password.text.toString()
            textShow.text = "$name and $pass"}
        remove.setOnClickListener { textShow.text = ""}




        return view
    }
}