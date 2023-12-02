package com.example.myapplication.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DatabaseSingleton
import com.example.myapplication.R
import com.example.myapplication.SignatureEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var startDate: String
    private lateinit var endDate: String
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 두 개의 RecyclerView를 위한 어댑터 인스턴스
    private lateinit var dateSortedAdapter: SignatureListAdapter
    private lateinit var nameSortedAdapter: SignatureListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calendar_fragment, container, false)

        val selectDateRangeButton = view.findViewById<Button>(R.id.selectDateRangeButton)

        // RecyclerView 초기화
        val dateSortedRecyclerView = view.findViewById<RecyclerView>(R.id.dateSortedRecyclerView)
        val nameSortedRecyclerView = view.findViewById<RecyclerView>(R.id.nameSortedRecyclerView)

        dateSortedAdapter = SignatureListAdapter()
        nameSortedAdapter = SignatureListAdapter()

        dateSortedRecyclerView.layoutManager = LinearLayoutManager(context)
        nameSortedRecyclerView.layoutManager = LinearLayoutManager(context)

        dateSortedRecyclerView.adapter = dateSortedAdapter
        nameSortedRecyclerView.adapter = nameSortedAdapter

        selectDateRangeButton.setOnClickListener {
            val today = Calendar.getInstance()
            showDatePicker(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), "시작 날짜 선택") { start ->
                startDate = start

                showDatePicker(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), "종료 날짜 선택") { end ->
                    endDate = end

                    val db = DatabaseSingleton.SignDB
                    val dao = db.signatureDao()
                    dao.getSignaturesInRange(startDate, endDate)
                        .observe(viewLifecycleOwner, Observer { signatures ->
                            // 데이터를 각 어댑터에 설정
                            dateSortedAdapter.setSignatures(signatures)
                            nameSortedAdapter.setSignatures(signatures.sortedBy { it.userName })
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

// SignatureListAdapter 및 ViewHolder 정의
class SignatureListAdapter : RecyclerView.Adapter<SignatureListAdapter.SignatureViewHolder>() {

    private var signatures = listOf<SignatureEntity>()

    fun setSignatures(newSignatures: List<SignatureEntity>) {
        signatures = newSignatures
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignatureViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.signature_list_item, parent, false)
        return SignatureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SignatureViewHolder, position: Int) {
        val signature = signatures[position]
        holder.bind(signature, position + 1) // 줄 번호는 position + 1로 설정
    }

    override fun getItemCount() = signatures.size

    class SignatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lineNumberTextView = itemView.findViewById<TextView>(R.id.lineNumberTextView)
        private val signatureDataTextView = itemView.findViewById<TextView>(R.id.signatureDataTextView)

        fun bind(signature: SignatureEntity, lineNumber: Int) {
            lineNumberTextView.text = "$lineNumber"
            signatureDataTextView.text = "${signature.userName}, ${signature.currentDate}"
        }
    }
}
