package com.example.checkpoint.application.services

import com.example.checkpoint.core.backend.api.appwrite.RetrofitInstance
import com.example.checkpoint.core.backend.api.request.Content
import com.example.checkpoint.core.backend.api.request.GenerateContentRequest
import com.example.checkpoint.core.backend.api.request.Part

suspend fun cancelSubscriptionURL(subscriptionName: String): String? {

    val promptText = """
    Provide the official cancellation page URL or relevant link for the subscription service "$subscriptionName".
    Only return the URL, nothing else.""".trimIndent()

    val request = GenerateContentRequest(
        contents = listOf(
            Content(
                parts = listOf(
                    Part(text = promptText)
                )
            )
        )
    )

    return try {
        val response = RetrofitInstance.api.generateContent("AIzaSyAO3hyaPl7GqWhMCz2_S0MTN9jst5XFxeE", request)

        if (response.isSuccessful) {
            response.body()?.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text
        } else {
            println("Error: ${response.code()} - ${response.errorBody()?.string()}")
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}