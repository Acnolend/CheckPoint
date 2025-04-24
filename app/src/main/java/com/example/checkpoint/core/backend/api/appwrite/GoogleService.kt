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


    suspend fun getInboxEmails(accessToken: String): List<String> {
        val subscriptions = mutableListOf<String>()
        val url = URL("https://gmail.googleapis.com/gmail/v1/users/me/messages?maxResults=200")
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
                val response = StringBuilder()
                var line: String?
                while (withContext(Dispatchers.IO) {
                        reader.readLine()
                    }.also { line = it } != null) {
                    response.append(line)
                }
                withContext(Dispatchers.IO) {
                    reader.close()
                }
                Log.d(TAG, "Raw response: $response")


                val jsonResponse = JSONObject(response.toString())
                val messages = jsonResponse.getJSONArray("messages")
                Log.d(TAG, "Found ${messages.length()} messages")

                for (i in 0 until messages.length()) {
                    val messageId = messages.getJSONObject(i).getString("id")
                    Log.d(TAG, "Message ID: $messageId")
                    subscriptions.add("Sub detected $messageId")
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
        Log.d(TAG, "Final subscriptions list size: ${subscriptions.size}")
        return subscriptions
    }


    private fun getMessageDetails(messageId: String, accessToken: String): JSONObject {
        val url = URL("https://gmail.googleapis.com/gmail/v1/users/me/messages/$messageId")
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Authorization", "Bearer $accessToken")
        connection.requestMethod = "GET"

        val response = StringBuilder()
        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream, StandardCharsets.UTF_8))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
            } else {
                println("Error al obtener los detalles del mensaje: $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }

        // Procesar la respuesta JSON
        return JSONObject(response.toString())
    }

    // Método para verificar si el mensaje está relacionado con una suscripción
    private fun isSubscriptionEmail(messageDetails: JSONObject): Boolean {
        val subject = getMessageSubject(messageDetails)
        // Verificar si el asunto contiene palabras clave relacionadas con suscripciones
        return subject.contains("subscription", ignoreCase = true) || subject.contains("payment", ignoreCase = true)
    }

    // Método para extraer el asunto de un mensaje
    private fun getMessageSubject(messageDetails: JSONObject): String {
        val headers = messageDetails.getJSONObject("payload").getJSONArray("headers")
        for (i in 0 until headers.length()) {
            val header = headers.getJSONObject(i)
            if (header.getString("name") == "Subject") {
                return header.getString("value")
            }
        }
        return ""
    }
}