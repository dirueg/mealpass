package com.example.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.myapplication.DatabaseSingleton

class CalendarFragment : androidx.fragment.app.Fragment() {
    // 여기에 캘린더 뷰 로직 및 Room 데이터베이스에서 해당 기간의 목록을 불러오는 로직 구현
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
        val db = DatabaseSingleton.SignDB
        val dao = db.signatureDao()
        dao.getAllSignatures().observe(viewLifecycleOwner, Observer {sign ->
            for (sg in sign){
                Log.d("CalendarFragment", sg.userName + ", " + sg.currentDate)
            }
        })
         */
        return null;
    }
}
