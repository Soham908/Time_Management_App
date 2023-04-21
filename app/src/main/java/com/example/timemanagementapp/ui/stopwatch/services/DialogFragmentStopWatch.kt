package com.example.timemanagementapp.ui.stopwatch.services

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.example.timemanagementapp.R
import com.example.timemanagementapp.recyclerviewAdapter.stopwatch.StructureStopWatch
import com.example.timemanagementapp.ui.stopwatch.StopWatchFragment
import com.google.firebase.firestore.FirebaseFirestore


class DialogFragmentStopWatch(val item: StructureStopWatch) : DialogFragment() {

    // Initialize the views
    private lateinit var editText1: EditText
    private lateinit var editText2: EditText
    private lateinit var editText3: EditText
    private lateinit var okButton: Button
    private lateinit var cancelButton: Button
    // custom dialog listener for transferring data to other fragment
//    lateinit var dialogListener: CustomDialogListener
    // now im adding the value directly to the firestore db but it is not recommended, change it afterwards
    lateinit var firestore: FirebaseFirestore
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

//            dialogListener = context as CustomDialogListener
            firestore = FirebaseFirestore.getInstance()


            // Set up the views
            editText1 = view.findViewById(R.id.editText1)
            editText2 = view.findViewById(R.id.editText2)
//            editText3 = view.findViewById(R.id.editText3)
            okButton = view.findViewById(R.id.dialogOkay)
            cancelButton = view.findViewById(R.id.dialogCancel)

            // Set up the buttons
            okButton.setOnClickListener {
                // Handle the OK button click
                input1 = editText1.text.toString()
                input2 = editText2.text.toString()
//                val input3 = editText3.text.toString()
                Toast.makeText(requireContext(), "$input1 : $input2 ", Toast.LENGTH_SHORT).show()

                // Do something with the inputs
//                dialogListener.onDialogPositiveClickListenerCustom(input1, input2, input3)
                // for now add to list afterwards handle to database
                addToList()
                dismiss()
            }

            cancelButton.setOnClickListener {
                // Handle the Cancel button click
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
