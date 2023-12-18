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
        saveImageToFile(bitmap)
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

    private fun displayImage(bitmap: Bitmap) {
        imageView.setImageBitmap(bitmap)
    }

    @SuppressLint("Range")
    private fun saveImageToFile(bitmap: Bitmap) {
        val imgService = ImageService()
        val timestamp = System.currentTimeMillis()
        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                try {
                    val outputStream = contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {
                            Log.e("TAG", "saveBitmapImage: ", e)
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    contentResolver.update(uri, values, null, null)
                    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
                    if (cursor != null){
                        cursor.moveToNext()
                        val path = cursor.getString(cursor.getColumnIndex("_data"))
                        cursor.close()
                        imgService.apiCall(File(path))
                    }
                } catch (e: Exception) {
                    Log.e("TAG", "saveBitmapImage: ", e)
                }
            }
        } else {
            val imageFileFolder = File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name))
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e("TAG", "saveBitmapImage: ", e)
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                // 이미지 저장 성공 메시지 또는 액션을 여기에 추가합니다.
                Log.d("SettingFragment", "파일 저장 성공")

                imgService.apiCall(imageFile)
            } catch (e: Exception) {
                Log.e("TAG", "saveBitmapImage: ", e)
            }
        }
    }
}