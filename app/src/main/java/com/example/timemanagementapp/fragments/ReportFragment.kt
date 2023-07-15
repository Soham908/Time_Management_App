package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemanagementapp.MainActivity.Companion.username
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentReportBinding
import com.example.timemanagementapp.structure_data_class.StructureStopWatch
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.collections.ArrayList

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    lateinit var firestore: FirebaseFirestore
    var listR = mutableListOf<StructureStopWatch>()

    private var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private val currentDate: LocalDate = LocalDate.now()
    private val month = currentDate.month.toString()
    private val currentWeekOfMonth = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfMonth())
    private val year: Int = currentDate.year

//    lateinit var barChart: BarChart
    lateinit var pieChart: PieChart
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_report, container, false)
        binding = FragmentReportBinding.bind(view)

//        val click = binding.button
//        barChart = binding.barChart
        pieChart = binding.pieChart
        getTimeList()
//        click.setOnClickListener{ getTimeList() }

        binding.selectDateButton.setOnClickListener{
            showDatePicker()
        }


        return view
    }

//    private fun recyclerView()
//    {
//        val recyclerView = binding.timeRecyclerView
//
//        recyclerView.layoutManager = LinearLayoutManager(context)
//    }


    @Suppress("UNCHECKED_CAST")
    @SuppressLint("NotifyDataSetChanged")
    private fun getTimeList(){
        firestore = FirebaseFirestore.getInstance()
        val documentRef = firestore.document("/Users_Collection/$username/More_Details/TimeRecord/$year/$month/weeks/week${currentWeekOfMonth-1}")
        documentRef.addSnapshotListener{ value, error ->
            if (error != null){
                return@addSnapshotListener
            }
            value?.get(date) ?: return@addSnapshotListener

            val lapList = value.get(date) as List<Map<*, *>>
            val lapObject = lapList.map { map ->
                StructureStopWatch(
                    id = map["id"]?.toString()?.toInt(),
                    description = map["description"].toString(),
                    time = map["time"].toString(),
                    work = map["work"].toString()
                )
            }
            listR.clear()
            for (lap in lapObject){
                listR.add(lap)
            }

            Log.d("dataFirebase", " this is from report $listR")
            mapGraph()
        }

    }

    private fun mapGraph() {
//        val barList = ArrayList<BarEntry>()
//
//        barList.add(BarEntry(100f,100f))
//        barList.add(BarEntry(101f,200f))
//        barList.add(BarEntry(102f,300f))
//        barList.add(BarEntry(103f,400f))
//        barList.add(BarEntry(104f,500f))
//
//
//        val barDataSet = BarDataSet(barList, "list")
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)
//        barDataSet.valueTextColor = Color.WHITE
//        val barData = BarData(barDataSet)
//        barChart.setFitBars(true)
//        barChart.data = barData
//
//        barChart.description.text = "trial bar"
//        barChart.animateY(2000)

        val list:ArrayList<PieEntry> = ArrayList()
        BarEntry(100f, 100f, "work done")
//        list.add(PieEntry(100f,"100"))
//        list.add(PieEntry(101f,"101"))
//        list.add(PieEntry(102f,"102"))
//        list.add(PieEntry(103f,"103"))
//        list.add(PieEntry(104f,"104"))
        var totalTime = 0f

        for (task in listR){
            val lapTime = task.time.substring(task.time.length - 8)
            val timeInSeconds = timeToSeconds(lapTime)
            list.add(PieEntry(timeInSeconds, task.work))
            totalTime += timeInSeconds
        }
        val timeDiff = 86400 - totalTime
        if (timeDiff > 0){
            list.add(PieEntry(timeDiff, "not record"))
        }

        val pieDataSet= PieDataSet(list,"List")

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS,255)
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 15f

        val pieData= PieData(pieDataSet)

        pieChart.data= pieData

        pieChart.description.text= "Pie Chart"

        pieChart.centerText="Day's Activity"
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.BLACK)

        pieChart.animateY(1000)


    }

    fun timeToSeconds(time: String): Float {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()

        return (hours * 3600 + minutes * 60 + seconds).toFloat()
    }

    private fun showDatePicker() {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            // Update the calendar with the selected date
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Update the TextView with the selected date
            val selectedDate = formatDate(calendar.time)
            binding.selectDateTextView.text = selectedDate
            date = selectedDate
            getTimeList()
        }

        // Show the DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(), dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: Date): String {
        // Format the date using SimpleDateFormat
        val format = SimpleDateFormat("dd-MM-yyyy")

        return format.format(date)
    }


}
