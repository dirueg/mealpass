package com.example.myapplication.rest

import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit.*
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File


internal interface Service {
    @Multipart
    @POST("upload?key=7f0c785ebd9095078bc2cb623a0b8b43")
    fun postImage(@Part image: MultipartBody.Part): Call<ResponseBody>
}

class ImageService() {
    fun apiCall(f: File){
        val reqFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), f)
        val body = MultipartBody.Part.createFormData("image", f.getName(), reqFile)

        val service: Service =
            Builder()
                .baseUrl("https://api.imgbb.com/1/")
                .build()
                .create(Service::class.java)
        val req: Call<ResponseBody> = service.postImage(body)
        req.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                Log.d("ImageService", response.body()!!.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                call.let { Log.e("ImageService", t.message.toString()) }
            }
        })
    }
}