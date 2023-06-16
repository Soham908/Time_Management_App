package com.example.timemanagementapp.dialogCustom

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentDialogStopWatchBinding
import com.example.timemanagementapp.structure_data_class.StructureStopWatch


class DialogFragmentStopWatch(val item: StructureStopWatch) : DialogFragment() {

    private lateinit var userWork: EditText
    private lateinit var userDescription: EditText
    private lateinit var input1: String
    private lateinit var input2: String
    private lateinit var binding: FragmentDialogStopWatchBinding

    companion object
    {
        var descriptionList = MutableLiveData<String>()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_dialog_stop_watch, null)
            binding = FragmentDialogStopWatchBinding.bind(view)

            val work = arguments?.getString("subject")

            userWork = binding.timeDialogWork
            if (work != "default") {
                userWork.setText(work)
            }
            userWork.requestFocus()
            userDescription = binding.timeDialogDescription

            binding.timeDialogOkay.setOnClickListener {
                input1 = userWork.text.toString()
                input2 = userDescription.text.toString()

                addToList()
                dismiss()
            }

            binding.timeDialogCancel.setOnClickListener {
                dismiss()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(userWork, InputMethodManager.SHOW_IMPLICIT)
            }, 200)

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun addToList() {
        // currently it works, the value is set by using this line because the item is passed as a parameter in the constructor so
        // easily change the value of it, and then the mutable list is there just so that it can call the observer that can notify dataset changed
        item.work = input1
        descriptionList.value = input1
    }

}
