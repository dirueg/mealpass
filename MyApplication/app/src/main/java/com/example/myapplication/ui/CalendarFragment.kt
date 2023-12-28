package com.example.myapplication.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DatabaseSingleton
import com.example.myapplication.R
import com.example.myapplication.saveImageToFile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


data class UserSignatureStats(
    val userName: String,
    val dates: List<String>,
    val totalCount: Int
)

class CalendarFragment : Fragment() {

    private lateinit var startDate: String
    private lateinit var endDate: String
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // 두 개의 RecyclerView를 위한 어댑터 인스턴스
    private lateinit var nameSortedAdapter: SignatureListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.calendar_fragment, container, false)
        val showDatePick = view.findViewById<TextView>(R.id.showdatepick)
        val selectDateRangeButton = view.findViewById<Button>(R.id.selectDateRangeButton)

        // RecyclerView 초기화
        val nameSortedRecyclerView = view.findViewById<RecyclerView>(R.id.nameSortedRecyclerView)


        nameSortedAdapter = SignatureListAdapter()


        nameSortedRecyclerView.layoutManager = LinearLayoutManager(context)


        nameSortedRecyclerView.adapter = nameSortedAdapter


        selectDateRangeButton.setOnClickListener {
            val today = Calendar.getInstance()

            showDatePicker(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), "시작 날짜 선택") { start ->
                startDate = start + " 00:00"

                showDatePicker(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH), "종료 날짜 선택") { end ->
                    endDate = end + " 23:59"

                    val db = DatabaseSingleton.SignDB
                    val dao = db.signatureDao()
                    dao.getSignaturesSortedByName(startDate, endDate)
                        .observe(viewLifecycleOwner, Observer { signatures ->
                            val stats = signatures.groupBy { it.userName }.map { entry ->
                                // 데이터를 각 어댑터에 설정

                                var userName = entry.key
                                var dates = entry.value.map { it.currentDate }
                                UserSignatureStats(
                                    userName = userName,
                                    dates = dates,
                                    totalCount = dates.size

                                )
                            }
                            var totalListCount = 0
                            for (userStats in stats) {
                                totalListCount += userStats.totalCount
                            }
                            nameSortedAdapter.setUserStats(stats)
                            showDatePick.text = startDate+ " ~ " +endDate + "       조회된 전체 식사 횟수는 " + totalListCount+"회 입니다."

                            var thisView = this.requireView()
                            thisView.post{
                                if (thisView.measuredWidth <= 0 || thisView.measuredHeight <= 0) {
                                    //Err
                                }

                                nameSortedRecyclerView.measure(
                                    View.MeasureSpec.makeMeasureSpec(
                                        nameSortedRecyclerView.getWidth(),
                                        View.MeasureSpec.EXACTLY
                                    ),
                                    View.MeasureSpec.makeMeasureSpec(
                                        0,
                                        View.MeasureSpec.UNSPECIFIED
                                    )
                                )

                                val bm = Bitmap.createBitmap(
                                    nameSortedRecyclerView.width,
                                    nameSortedRecyclerView.measuredHeight,
                                    Bitmap.Config.ARGB_8888
                                )
                                nameSortedRecyclerView.draw(Canvas(bm))
                                val im = ImageView(activity)
                                im.setImageBitmap(bm)
                                AlertDialog.Builder(activity).setView(im).show()
                                saveImageToFile(nameSortedRecyclerView, bm)
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

// SignatureListAdapter 및 ViewHolder 정의
class SignatureListAdapter : RecyclerView.Adapter<SignatureListAdapter.SignatureViewHolder>() {

    private var userStats = listOf<UserSignatureStats>()

    fun setUserStats(newStats: List<UserSignatureStats>) {
        userStats = newStats
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignatureViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.signature_list_item, parent, false)
        return SignatureViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SignatureViewHolder, position: Int) {
        val stat = userStats[position]
        holder.bind(stat)
    }

    override fun getItemCount() = userStats.size

    class SignatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView = itemView.findViewById<TextView>(R.id.userNameTextView)
        private val datesTextView = itemView.findViewById<TextView>(R.id.datesTextView)
        private val totalCountTextView = itemView.findViewById<TextView>(R.id.totalCountTextView)

        fun bind(stat: UserSignatureStats) {
            userNameTextView.text = stat.userName
            datesTextView.text = stat.dates.joinToString(separator = "\n")+"\n"
            totalCountTextView.text = "총 합: ${stat.totalCount}"
        }
    }
}
