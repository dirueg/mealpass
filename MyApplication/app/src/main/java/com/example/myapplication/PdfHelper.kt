package com.example.myapplication

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.provider.MediaStore
import android.util.Log
import java.io.FileOutputStream

fun saveBitmapToPdf(bitmap: List<Bitmap>, contentResolver: ContentResolver, titleText: String){
    val doc = PdfDocument()
    val firstPageInfo = PdfDocument.PageInfo.Builder(bitmap.first().width, 1500, 1).create()
    val firstPage = doc.startPage(firstPageInfo)
    val title = Paint()
    title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

    // below line is used for setting text size
    // which we will be displaying in our PDF file.
    title.textSize = firstPageInfo.pageWidth / 20f

    // below line is sued for setting color
    // of our text inside our PDF file.
    title.color = Color.BLACK
    titleText.split("\n")
        .forEachIndexed { index, str ->
            firstPage.canvas.drawText(str, title.textSize, title.textSize * (index + 1), title)
        }

    doc.finishPage(firstPage)

    var pageNumber = 2
    var pageInfo = PdfDocument.PageInfo.Builder(bitmap.first().width, 1500, pageNumber).create()
    var page = doc.startPage(pageInfo)
    var topOffset = 0f
    bitmap.forEach{value ->
        if(pageInfo.pageHeight - topOffset < value.height){
            doc.finishPage(page)
            pageInfo = PdfDocument.PageInfo.Builder(bitmap.first().width, 1500, ++pageNumber).create()
            page = doc.startPage(pageInfo)
            topOffset = 0f
        }
        page.canvas.drawBitmap(
            value,
            0f,
            topOffset,
            null
        )
        topOffset += value.height
    }
    doc.finishPage(page)

    val values = ContentValues()
    values.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString() + ".pdf")
    values.put(MediaStore.MediaColumns.MIME_TYPE, "files/pdf")
    values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents")
    values.put(MediaStore.Video.Media.TITLE, "SomeName")
    values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
    values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
    values.put(MediaStore.Files.FileColumns.IS_PENDING, true)
    MediaStore.Files.getContentUri("external")
    val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values);

    if(uri != null){
        val pfd = contentResolver.openFileDescriptor(uri, "w")
        val outputStream = FileOutputStream(pfd!!.fileDescriptor)
        try {
            doc.writeTo(outputStream)
            outputStream.close()
            doc.close()
        } catch (e: Exception) {
            Log.e("PdfHelper", "saveBitmapImage: ", e)
        }
        values.put(MediaStore.Files.FileColumns.IS_PENDING, false)
        contentResolver.update(uri, values, null, null)
    }
}