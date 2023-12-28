package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import com.example.myapplication.rest.ImageService
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@SuppressLint("Range")
fun saveImageToFile(view: View, bitmap: Bitmap) {
    val imgService = ImageService()
    val timestamp = System.currentTimeMillis()
    val contentResolver = view.context.contentResolver
    //Tell the media scanner about the new file so that it is immediately available to the user.
    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
        values.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            "Pictures/" + view.context.getString(R.string.app_name)
        )
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
                if (cursor != null) {
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
        val imageFileFolder = File(
            Environment.getExternalStorageDirectory().toString() + '/' + view.context.getString(
                R.string.app_name
            )
        )
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