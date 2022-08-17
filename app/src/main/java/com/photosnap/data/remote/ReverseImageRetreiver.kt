package com.photosnap.data.remote

import com.google.gson.GsonBuilder
import com.photosnap.data.model.ServerResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Part
import java.util.concurrent.TimeUnit


class ReverseImageRetreiver {

    private val reverseImageRetreiver: ReverseImageNetworkInterface

    companion object{
        var BASE_URL =  "http://api-edu.gtl.ai/api/v1/imagesearch/"
    }

    init {
        val client = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2,TimeUnit.MINUTES)
            .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        reverseImageRetreiver = retrofit.create(ReverseImageNetworkInterface::class.java)
    }

    fun uploadInverseImage(@Part image: MultipartBody.Part): Call<ResponseBody>  = reverseImageRetreiver.uploadImageToServer(image)

    suspend fun googleInverseImage(url:String):List<ServerResponse> = reverseImageRetreiver.googleImageUrlToServer(url)

    suspend fun bingInverseImage(url:String):List<ServerResponse> = reverseImageRetreiver.bingImageUrlToServer(url)

    suspend fun tineyeInverseImage(url:String):List<ServerResponse> = reverseImageRetreiver.tineyeImageUrlToServer(url)

}