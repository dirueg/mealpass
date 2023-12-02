package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
            //                        1 -> showFragment(CalendarFragment())
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
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

