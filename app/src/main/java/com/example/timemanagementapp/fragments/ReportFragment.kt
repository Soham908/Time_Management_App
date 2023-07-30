package com.example.timemanagementapp.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.timemanagementapp.MainActivity.Companion.username
import com.example.timemanagementapp.R
import com.example.timemanagementapp.databinding.FragmentReportBinding
import com.example.timemanagementapp.structure_data_class.StructureStopWatch
import com.github.mikephil.charting.charts.BarChart
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.firestore.DocumentSnapshot
import java.time.Month
import kotlin.collections.ArrayList

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding
    private lateinit var firestore: FirebaseFirestore
    private var listReport = mutableListOf<StructureStopWatch>()
    private lateinit var valueStore: DocumentSnapshot

    private var date: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    private var currentDate: LocalDate = LocalDate.now()
    private var month = currentDate.month.toString()
    private var currentWeekOfMonth = currentDate.get(WeekFields.of(Locale.getDefault()).weekOfMonth())
    private var year: Int = currentDate.year

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private val calendar: Calendar = Calendar.getInstance()
    private var isPieChart = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_report, container, false)
        binding = FragmentReportBinding.bind(view)

        pieChart = binding.pieChart
        barChart = binding.barChart

        binding.selectDateButton.setOnClickListener{
            showDatePicker()
        }

        binding.reportChartSwitch.setOnCheckedChangeListener { compoundButton, boolean ->
            if (boolean) {
                isPieChart = false
                getTimeList()
            }
            else{
                isPieChart = true
                getTimeList()
            }
        }
        getTimeList()

        return view
    }


    @Suppress("UNCHECKED_CAST")
    @SuppressLint("NotifyDataSetChanged")
    private fun getTimeList(){
        firestore = FirebaseFirestore.getInstance()
        val documentRef = firestore.document("/Users_Collection/$username/More_Details/TimeRecord/$year/$month/weeks/week${currentWeekOfMonth}")
        documentRef.addSnapshotListener{ value, error ->
            if (error != null){
                return@addSnapshotListener
            }
            value?.get(date) ?: return@addSnapshotListener
            valueStore = value

            val lapList = value.get(date) as List<Map<*, *>>
            val lapObject = lapList.map { map ->
                StructureStopWatch(
                    id = map["id"]?.toString()?.toInt(),
                    description = map["description"].toString(),
                    time = map["time"].toString(),
                    work = map["work"].toString()
                )
            }
            listReport.clear()
            for (lap in lapObject){
                listReport.add(lap)
            }

            if (isPieChart){
                barChart.isGone = true
                pieChart.isVisible = true
                createPieChart()
            }
            else{
                pieChart.isGone = true
                barChart.isVisible = true
                createBarChart()
            }
        }

    }

    private fun createPieChart() {
        val list:ArrayList<PieEntry> = ArrayList()
        var totalTime = 0f

        for (task in listReport){
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
        pieChart.description.isEnabled = false
        pieChart.centerText="Day's Activity"
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(800)

    }

    @Suppress("UNCHECKED_CAST")
    private fun createBarChart() {
        val entries: MutableList<BarEntry> = mutableListOf()
        for ((count, days) in valueStore.data?.values!!.withIndex()){
            val lapList = days as List<Map<*, *>>
            val lapObject = lapList.map { map ->
                StructureStopWatch(
                    id = map["id"]?.toString()?.toInt(),
                    description = map["description"].toString(),
                    time = map["time"].toString(),
                    work = map["work"].toString()
                )
            }
            val dateList = valueStore.data?.keys
            val dates = mutableListOf<String>()
            for (datePick in dateList!!){
                val date = datePick.substring(0, 2)
                dates.add(date)
            }

            for (task in lapObject) {
                val lapTime = task.time.substring(task.time.length - 8)
                val timeInSeconds = timeToSeconds(lapTime)
                val barEntry = BarEntry(dates[count].toFloat(), timeInSeconds)
                entries.add(barEntry)
            }
        }

        val barDataSet = BarDataSet(entries, "Week Data")
        barDataSet.stackLabels = arrayOf("Work 1", "Work 2", "Work 3")

        barDataSet.setColors(
            Color.rgb(63, 81, 181),
            Color.rgb(255, 152, 0),
            Color.rgb(255, 193, 7)
        )

        val barData = BarData(barDataSet)

        barChart.data = barData

        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.setDrawValueAboveBar(true)

        val xAxis = barChart.xAxis
//        xAxis.valueFormatter = DayAxisValueFormatter()
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

        barChart.invalidate()
    }

    private fun timeToSeconds(time: String): Float {
        val parts = time.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()

        return (hours * 3600 + minutes * 60 + seconds).toFloat()
    }

    private fun showDatePicker() {
        val materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker()
        materialDatePickerBuilder.setSelection(calendar.timeInMillis)
        // it is important to take the date from the previously set calender details to properly display last date selected as current one

        val materialDatePicker = materialDatePickerBuilder.build()
        materialDatePicker.addOnPositiveButtonClickListener {

            calendar.timeInMillis = it

            val selectedDate = formatDate(calendar.time)
            binding.selectDateTextView.text = selectedDate
            date = selectedDate
            currentWeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH)
            month = Month.of(calendar.get(Calendar.MONTH) + 1 ).name
            getTimeList()
        }
        materialDatePicker.show(childFragmentManager, "date select")
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("dd-MM-yyyy")

        return format.format(date)
    }


}
