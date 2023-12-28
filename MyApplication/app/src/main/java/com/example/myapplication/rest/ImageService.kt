package com.example.myapplication.rest

import android.util.Log
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File


internal interface Service {
    @Multipart
    @POST("upload?key=7f0c785ebd9095078bc2cb623a0b8b43")
    fun postImage(@Part image: MultipartBody.Part): Call<JsonObject>
}

class ImageService() {
    fun apiCall(f: File, callback: Callback<JsonObject>){
        val reqFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f)
        val body = MultipartBody.Part.createFormData("image", f.name, reqFile)

        val service: Service =
            Builder()
                .baseUrl("https://api.imgbb.com/1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Service::class.java)
        val req: Call<JsonObject> = service.postImage(body)
        req.enqueue(callback)

    }
}