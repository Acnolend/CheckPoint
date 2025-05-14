package com.example.checkpoint.application.services

import com.example.checkpoint.BuildConfig
import com.example.checkpoint.core.backend.api.appwrite.RetrofitInstance
import com.example.checkpoint.core.backend.api.request.Content
import com.example.checkpoint.core.backend.api.request.GenerateContentRequest
import com.example.checkpoint.core.backend.api.request.Part
import com.example.checkpoint.core.store.CurrencyStore
import com.example.checkpoint.core.store.LanguageStore
import java.util.Locale

suspend fun generateSimilarSubscriptions(subscriptionName: String, subscriptionPrice: String): String? {
    val currentLocale = LanguageStore.getCurrentLanguage()
    val responseLanguage = currentLocale.getDisplayLanguage(Locale.ENGLISH)

    val currencySymbol = CurrencyStore.getCurrencySymbol()
    val formattedPrice = CurrencyStore.formatPrice(subscriptionPrice)

    val promptText = """
        Suggest up to 3 subscription services similar to "$subscriptionName" that offer a comparable service for a lower cost.
        The current subscription costs $formattedPrice per month.
        The currency used is $currencySymbol.
        Only list the service names and their approximate prices if possible.
        Respond in $responseLanguage.
    """.trimIndent()

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
        val response = RetrofitInstance.api.generateContent(BuildConfig.GEMINI_API, request)

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




