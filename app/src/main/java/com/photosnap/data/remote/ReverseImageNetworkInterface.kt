package com.photosnap.data.remote

import com.photosnap.data.model.ServerResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ReverseImageNetworkInterface {

    @Multipart
    @POST("upload")
    fun uploadImageToServer(
        @Part image : MultipartBody.Part
    ): Call<ResponseBody>

    @GET("bing")
    suspend fun bingImageUrlToServer(
        @Query("url") url: String,
    ): List<ServerResponse>

    @GET("google")
    suspend fun googleImageUrlToServer(
        @Query("url") url: String,
    ): List<ServerResponse>

    @GET("tineye")
    suspend fun tineyeImageUrlToServer(
        @Query("url") url: String,
    ): List<ServerResponse>
}