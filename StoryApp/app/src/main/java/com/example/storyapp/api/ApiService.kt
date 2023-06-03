package com.example.storyapp.api

import com.example.storyapp.model.LoginResponse
import com.example.storyapp.model.RegisterResponse
import com.example.storyapp.model.story.AllStoriesResponse
import com.example.storyapp.model.story.StoryPageResponse
import com.example.storyapp.model.story.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST ("register")
    fun userRegister(
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String?,
        @Query("page") page : Int = 1,
        @Query("size") size : Int = 25,
        @Query("location") location : Int = 1
    ) : Call<AllStoriesResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat : Double,
        @Part("lon") lon : Double
    ): Call<UploadStoryResponse>

    @GET("storie")
    suspend fun getStoriesPage(
        @Header("Authorization") token: String?,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int = 0
    ): StoryPageResponse

}