package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.myapplication.DatabaseSingleton
import com.example.myapplication.R
import com.example.myapplication.SignatureEntity
import com.example.myapplication.rest.ImageService
import com.example.myapplication.saveImageToFile
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class SettingFragment : Fragment() {
    private lateinit var dataList: List<SignatureEntity>
    private lateinit var imageView: ImageView
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
        DatabaseSingleton.SignDB.signatureDao().getAllSignatures().observe(viewLifecycleOwner, Observer { signs ->
            dataList = signs
        })
        imageView = view.findViewById(R.id.imgSignature)
        view.findViewById<Button>(R.id.btnGenerateImage).setOnClickListener {
            generateAndSaveImage(dataList)
        }
        contentResolver = view.context.contentResolver
    }

    private fun generateAndSaveImage(dataList: List<SignatureEntity>) {
        // 이미지 크기 및 기타 설정을 정의합니다.
        val imageWidth = 800
        val imageHeight = dataList.size * 100 // 각 행의 높이를 100px로 가정
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 데이터를 이미지에 그립니다.
        drawDataOnCanvas(canvas, dataList)

        // 이미지를 저장합니다.
        saveImageToFile(this.requireView(), bitmap)
    }

    private fun drawDataOnCanvas(canvas: Canvas, dataList: List<SignatureEntity>) {
        val paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 30f

        dataList.forEachIndexed { index, signatureEntity ->
            val yPosition = (index + 1) * 100f
            canvas.drawText(signatureEntity.userName, 10f, yPosition, paint)
            canvas.drawText(signatureEntity.currentDate, 200f, yPosition, paint)

            val signatureBitmap = BitmapFactory.decodeByteArray(
                signatureEntity.signature,
                0,
                signatureEntity.signature.size
            )
            canvas.drawBitmap(
                signatureBitmap,
                null,
                Rect(400, yPosition.toInt() - 80, 600, yPosition.toInt() + 20),
                paint
            )
        }
    }
}