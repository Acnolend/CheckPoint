package com.example.checkpoint.core.backend.api.appwrite

import android.util.Log
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

    suspend fun getInboxSubscriptions(accessToken: String): List<String> {
        val subscriptions = mutableListOf<String>()
        val url = URL("https://gmail.googleapis.com/gmail/v1/users/me/messages?maxResults=50")
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
                reader.close()

                val jsonResponse = JSONObject(response)
                val messages = jsonResponse.getJSONArray("messages")
                Log.d(TAG, "Found ${messages.length()} messages")

                for (i in 0 until messages.length()) {
                    val messageId = messages.getJSONObject(i).getString("id")
                    Log.d(TAG, "Checking Message ID: $messageId")

                    val messageDetails = getMessageDetails(messageId, accessToken)

                    if (isSubscriptionEmail(messageDetails)) {
                        Log.d(TAG, "Subscription email found: $messageId")
                        subscriptions.add("Sub detected $messageId")
                    }
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

        Log.d(TAG, "Total subscriptions detected: ${subscriptions.size}")
        return subscriptions
    }

    private fun getMessageDetails(messageId: String, accessToken: String): JSONObject {
        val url = URL("https://gmail.googleapis.com/gmail/v1/users/me/messages/$messageId?format=metadata")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        connection.requestMethod = "GET"

        val response = StringBuilder()
        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
                response.append(reader.readText())
                reader.close()
            } else {
                Log.e(TAG, "Error fetching message details: $responseCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching message details", e)
        } finally {
            connection.disconnect()
        }

        return JSONObject(response.toString())
    }

    private fun isSubscriptionEmail(messageDetails: JSONObject): Boolean {
        val subject = getHeaderValue(messageDetails, "Subject")
        val from = getHeaderValue(messageDetails, "From")
        val listUnsubscribe = getHeaderValue(messageDetails, "List-Unsubscribe")

        return subject.contains("subscription", ignoreCase = true) ||
                subject.contains("payment", ignoreCase = true) ||
                listUnsubscribe.isNotEmpty() ||
                from.contains("noreply", ignoreCase = true)
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
}
