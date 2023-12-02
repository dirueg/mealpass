package com.example.myapplication.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.myapplication.DatabaseSingleton
import com.example.myapplication.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var startDate: String
    private lateinit var endDate: String
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calendar_fragment, container, false)

        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        val db = DatabaseSingleton.SignDB
        val dao = db.signatureDao()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 시작 날짜 선택기 표시
            showDatePicker(year, month, dayOfMonth, "시작 날짜 선택") { start ->
                startDate = start

                // 종료 날짜 선택기 표시
                showDatePicker(year, month, dayOfMonth, "종료 날짜 선택") { end ->
                    endDate = end

                    dao.getSignaturesInRange(startDate, endDate).observe(viewLifecycleOwner, Observer { signatures ->
                        for (signature in signatures) {
                            Log.d("CalendarFragment", "${signature.userName}, ${signature.currentDate}")
                            // 추가적인 로직, 예: 서명 목록을 화면에 표시
                        }
                    })
                }
            }
        }

        return view
    }

    private fun showDatePicker(year: Int, month: Int, dayOfMonth: Int, title: String, callback: (String) -> Unit) {
        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDayOfMonth)
            }
            callback(dateFormat.format(selectedDate.time))
        }, year, month, dayOfMonth)

        datePickerDialog.setTitle(title)
        datePickerDialog.show()
    }
}