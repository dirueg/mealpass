package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
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
import kotlin.math.ceil
import kotlin.math.max

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
            } catch (e: Exception) {
                Log.e("TAG", "saveBitmapImage: ", e)
            }
        }
        Log.d("BitmapHelper", "파일 저장이랑 뭐 한듯ㄴ")
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
        } catch (e: Exception) {
            Log.e("TAG", "saveBitmapImage: ", e)
        }
    }
}

fun getBitmaps(bitmap: Bitmap, maxSize: Int): List<Bitmap> {
    val width = bitmap.width
    val height = bitmap.height

    val nChunks = ceil(max(width, height) / maxSize.toDouble())
    val bitmaps: MutableList<Bitmap> = ArrayList()

    var start = 0
    for (i in 1..nChunks.toInt()) {
        bitmaps.add(
            if (width >= height){
                var croppedWidth = maxSize
                if(start + croppedWidth > bitmap.width){
                    croppedWidth = bitmap.width - start
                }
                Bitmap.createBitmap(bitmap, start, 0, croppedWidth, height)
            }
            else{
                var croppedHeight = maxSize
                if(start + croppedHeight > bitmap.height){
                    croppedHeight = bitmap.height - start
                }
                Bitmap.createBitmap(bitmap, 0, start, width, croppedHeight)
            }
        )
        start += maxSize
    }

    return bitmaps
}