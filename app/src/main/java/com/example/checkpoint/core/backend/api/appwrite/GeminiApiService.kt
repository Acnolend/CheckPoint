package com.example.checkpoint.core.backend.api.appwrite

import com.example.checkpoint.core.backend.api.request.GenerateContentRequest
import com.example.checkpoint.core.backend.api.response.GenerateContentResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): Response<GenerateContentResponse>
}

object RetrofitInstance {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val api: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }
}