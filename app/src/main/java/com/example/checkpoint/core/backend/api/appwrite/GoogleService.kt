package com.example.checkpoint.core.backend.api.appwrite

import android.util.Base64
import android.util.Log
import com.example.checkpoint.BuildConfig
import com.example.checkpoint.core.backend.api.request.Content
import com.example.checkpoint.core.backend.api.request.GenerateContentRequest
import com.example.checkpoint.core.backend.api.request.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class GoogleService {

    private val TAG = "com.example.checkpoint.core.backend.api.appwrite.GoogleService"

    suspend fun getInboxSubscriptions(accessToken: String): String {
        val url = URL("https://gmail.googleapis.com/gmail/v1/users/me/messages?maxResults=100")
        val connection = withContext(Dispatchers.IO) {
            url.openConnection()
        } as HttpURLConnection

        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        connection.requestMethod = "GET"

        try {
            val responseCode = connection.responseCode
            Log.d(TAG, "Response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
                val response = reader.readText()
                withContext(Dispatchers.IO) {
                    reader.close()
                }

                val jsonResponse = JSONObject(response)
                val messages = jsonResponse.getJSONArray("messages")
                val emailSummaries = StringBuilder()

                for (i in 0 until messages.length()) {
                    val messageId = messages.getJSONObject(i).getString("id")
                    val messageDetails = getMessageDetails(messageId, accessToken)

                    val subject = getHeaderValue(messageDetails, "Subject")
                    val from = getHeaderValue(messageDetails, "From")
                    val bodyText = messageDetails.optString("bodyText", "No body found")

                    if (isRelevantSubject(subject)) {
                        emailSummaries.append("Email $i:\nFrom: $from\nSubject: $subject\nBody: $bodyText\n\n")
                    }
                }

                val query = """
                    You are a subscription management assistant.
                    From the following list of emails, detect subscriptions and return their details as a JSON array in this exact format:
                    
                    [
                      {
                        "subscriptionName": "string",
                        "subscriptionRenewalDate": "YYYY-MM-DD",
                        "subscriptionCost": float,
                        "subscriptionCostType": "DAILY|WEEKLY|BIWEEKLY|MONTHLY|BIMONTHLY|QUARTERLY|SEMIANNUAL|ANNUAL"
                      }
                    ]
                    
                    Important:
                    - If the renewal date is not explicitly mentioned, assume it's the date when the email was sent.
                    - The subscription cost type can only be one of these: DAILY, WEEKLY, BIWEEKLY, MONTHLY, BIMONTHLY, QUARTERLY, SEMIANNUAL, ANNUAL.
                    - If you can't find a cost or type, leave the field empty or null.
                    - Ignore promotional emails or newsletters not related to paid subscriptions.
                    
                    Emails:
                    $emailSummaries
                    """.trimIndent()



                val aiResponse = fetchSubscriptionDetails(query)


                if (aiResponse != null && aiResponse != "[]") {
                    return aiResponse
                } else {
                    Log.d(TAG, "No subscriptions detected, returning empty list.")
                }

            } else {
                Log.e(TAG, "Failed to fetch emails: $responseCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while fetching emails", e)
        } finally {
            connection.disconnect()
            Log.d(TAG, "Connection closed")
        }

        return "[]"
    }

    private fun getMessageDetails(messageId: String, accessToken: String): JSONObject {
        val url = URL("https://gmail.googleapis.com/gmail/v1/users/me/messages/$messageId?format=full")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        connection.requestMethod = "GET"

        val response = StringBuilder()
        try {
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8)).use { reader ->
                    response.append(reader.readText())
                }
            } else {
                Log.e(TAG, "Error fetching message details: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching message details", e)
        } finally {
            connection.disconnect()
        }

        val jsonResponse = JSONObject(response.toString())

        val payload = jsonResponse.getJSONObject("payload")
        val bodyText = extractBodyFromPayload(payload)
        jsonResponse.put("bodyText", bodyText)
        return jsonResponse
    }

    private fun extractBodyFromPayload(payload: JSONObject): String {
        val body = payload.optJSONObject("body")
        if (body != null && body.optString("data").isNotEmpty()) {
            val encoded = body.optString("data")
            return decodeBase64(encoded)
        }

        val parts = payload.optJSONArray("parts") ?: return ""
        for (i in 0 until parts.length()) {
            val part = parts.getJSONObject(i)
            val mimeType = part.optString("mimeType")

            if (mimeType == "text/plain") {
                val data = part.getJSONObject("body").optString("data")
                if (data.isNotEmpty()) {
                    return decodeBase64(data)
                }
            }

            part.optJSONArray("parts")?.let { _ ->
                val nested = extractBodyFromPayload(part)
                if (nested.isNotEmpty()) return nested
            }
        }
        return ""
    }

    private fun decodeBase64(encoded: String): String {
        val flags = Base64.URL_SAFE or Base64.NO_WRAP
        val bytes = Base64.decode(encoded, flags)
        return String(bytes, Charsets.UTF_8)
    }

    private fun getHeaderValue(messageDetails: JSONObject, headerName: String): String {
        val headers = messageDetails.getJSONObject("payload").getJSONArray("headers")
        for (i in 0 until headers.length()) {
            val header = headers.getJSONObject(i)
            if (header.getString("name").equals(headerName, ignoreCase = true)) {
                return header.getString("value")
            }
        }
        return ""
    }

    private fun isRelevantSubject(subject: String): Boolean {
        val keywords = listOf(
            "renueva", "renovación", "vence", "suscripción", "membresía",
            "factura", "recibo", "pago", "confirmación", "último aviso",
            "recordatorio", "interrupción", "plan", "servicio", "beneficio",
            "promoción", "descuento", "vence hoy", "tu cuenta", "renovar",
            "renew", "renewal", "expires", "subscription", "membership",
            "invoice", "receipt", "payment", "confirmation", "last notice",
            "reminder", "interruption", "plan", "service", "benefit",
            "promotion", "discount", "due today", "your account", "auto-renew"
        )

        return keywords.any { subject.contains(it, ignoreCase = true) }
    }

    private suspend fun fetchSubscriptionDetails(query: String): String? {
        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = query)
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

}
