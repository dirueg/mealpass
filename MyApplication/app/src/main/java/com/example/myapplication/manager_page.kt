package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.ui.CalendarFragment
import com.example.myapplication.ui.ListFragment
import com.google.android.material.tabs.TabLayout

class manager_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_page)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    showFragment(ListFragment())
                    when (tab.position) {
                        0 -> showFragment(ListFragment())
                        1 -> showFragment(CalendarFragment())
            //                    2 -> showFragment(SettingFragment())
                        // 다른 탭에 대한 처리
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // 탭 선택이 해제될 때의 처리
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // 이미 선택된 탭이 다시 선택될 때의 처리
            }
        })
        // 이건 ui select observe 꼼수인데, 강제로 다른 탭을 선택했다가 0번 탭을 선택하게 하면
        // 0번 탭 선택한걸 그제서야 인식해서 제대로 탭이 선택된걸로 인식, onTabSelected가 실행됨
        // 결론은 이제 관리자모드 드가면 바로 직원 리스트 탭으로 넘어가고 있음
        tabLayout.getTabAt(1)?.select()
        tabLayout.getTabAt(0)?.select()
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

