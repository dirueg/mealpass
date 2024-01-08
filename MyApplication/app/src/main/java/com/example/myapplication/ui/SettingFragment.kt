package com.example.myapplication.ui

import android.content.ContentResolver
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.DatabaseSingleton
import com.example.myapplication.R
import com.example.myapplication.SignatureEntity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingFragment : Fragment() {
    private lateinit var dataList: List<SignatureEntity>
    private lateinit var contentResolver: ContentResolver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.setting_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signDao = DatabaseSingleton.SignDB.signatureDao()
        signDao.getAllSignatures().observe(viewLifecycleOwner) { signs ->
            dataList = signs
        }

        // RecyclerView 초기화
        val nameSortedRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerUser)
        val nameSortedAdapter = SignatureListAdapter()
        nameSortedRecyclerView.layoutManager = LinearLayoutManager(context)
        nameSortedRecyclerView.adapter = nameSortedAdapter

        val spinner = view.findViewById<Spinner>(R.id.userSpinner)
        DatabaseSingleton.AppDB.userDao().getAll()
            .observe(viewLifecycleOwner) { users ->
                ArrayAdapter(
                    view.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    users.map { user ->
                        user.name
                    }).also { adapter ->
                    spinner.adapter = adapter
                }
            }
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val userName = parent.getItemAtPosition(position) as String
                signDao.getSignaturesByName(userName).observe(viewLifecycleOwner) { signs ->
                    nameSortedAdapter.setUserStats(
                        listOf(
                            UserSignatureStats(
                                userName = userName,
                                dates = signs.map { sign -> sign.currentDate },
                                totalCount = signs.size
                            )
                        )
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        contentResolver = view.context.contentResolver
        val inputBtn = view.findViewById<Button>(R.id.queryBtn)
        inputBtn.setOnClickListener {
            val textInput = view.findViewById<TextInputLayout>(R.id.textInputLayout)
            val input = textInput.editText!!.text.toString()
            val name = spinner.selectedItem.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val signList = signDao.getSignaturesByNameAndDate(name, input)
                if (signList.isNotEmpty()) {
                    signDao.deleteSignatures(signList.map { entity -> entity.id })
                } else {
                    signDao.insertSignature(
                        SignatureEntity(
                            userName = name,
                            currentDate = input,
                            signature = ByteArray(0)
                        )
                    )
                }
            }
        }
    }
}