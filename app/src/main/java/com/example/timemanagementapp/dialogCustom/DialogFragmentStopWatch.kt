package com.example.timemanagementapp.dialogCustom

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.example.timemanagementapp.R
import com.example.timemanagementapp.structure_data_class.StructureStopWatch


class DialogFragmentStopWatch(val item: StructureStopWatch) : DialogFragment() {

    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var okButton: Button
    private lateinit var cancelButton: Button
    private lateinit var input1: String
    private lateinit var input2: String

    companion object
    {
        var descriptionList = MutableLiveData<String>()
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.fragment_dialog_stop_watch, null)

            val work = arguments?.getString("subject")

            editText1 = view.findViewById(R.id.editText1)
            editText1.setText(work)
            editText2 = view.findViewById(R.id.editText2)
            okButton = view.findViewById(R.id.dialogOkay)
            cancelButton = view.findViewById(R.id.dialogCancel)

            okButton.setOnClickListener {
                input1 = editText1.text.toString()
                input2 = editText2.text.toString()

                addToList()
                dismiss()
            }

            cancelButton.setOnClickListener {
                dismiss()
            }

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
