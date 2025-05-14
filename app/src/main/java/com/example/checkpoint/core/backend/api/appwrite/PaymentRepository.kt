package com.example.checkpoint.core.backend.api.appwrite

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.BuildConfig
import io.appwrite.Query
import org.json.JSONObject


class PaymentRepository(private val appwriteService: AppwriteService) {

    private val databaseId = BuildConfig.DATABASE_ID
    private val collectionId = BuildConfig.PAYMENT_COLLECTION

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun savePayment(userId: String, name: String, documentId: String, amount: Double, date: String) {
        val documentId = documentId
        val dataMap = mapOf(
            "name" to name,
            "cost" to amount,
            "renewalDate" to date,
            "userId" to userId
        )
        appwriteService.save(collectionId, documentId, dataMap)
    }

    suspend fun deleteAllPayments(userId: String) {
        val query = listOf(Query.equal("userId", userId))
        val queryStrings = query.map { it.toString() }
        val allPayments = appwriteService.getAll(databaseId, collectionId, queryStrings)

        allPayments.forEach { data ->
            val paymentId = data["\$id"] as? String ?: return@forEach
            appwriteService.delete(databaseId, collectionId, paymentId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllPayments(userId: String): List<JSONObject> {
        val query = listOf(Query.equal("userId", userId))
        val queryStrings = query.map { it.toString() }
        val allPayments = appwriteService.getAll(databaseId, collectionId, queryStrings)

        return allPayments.map { data ->
            val jsonObject = JSONObject()
            jsonObject.put("subscriptionName", data["name"] as? String ?: "")
            jsonObject.put("amount", (data["cost"] as? Number)?.toString() ?: "0.0")
            jsonObject.put("date", data["renewalDate"] as? String ?: "")
            jsonObject
        }
    }
}