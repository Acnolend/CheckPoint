package com.example.checkpoint.core.backend.api.appwrite

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.application.services.isDefaultImageUrl
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionRenewalDate
import io.appwrite.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SubscriptionRepository(private val appwriteService: AppwriteService) {

    private val databaseId = "67f16b4800153970e87a"
    private val collectionId = "67f1730c001c5348bb6a"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveSubscription(userId: String, subscription: Subscription, documentId: String) {

        val documentId = documentId
        val dataMap = mapOf(
            "name" to subscription.name.name,
            "image" to subscription.image.image.toString(),
            "cost" to subscription.cost.cost,
            "typecost" to subscription.cost.type.toString(),
            "reminder" to subscription.reminder.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "renewalDate" to subscription.renewalDate.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "userId" to userId
        )
        appwriteService.save(collectionId, documentId, dataMap)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateSubscription(subscriptionId: String, userId: String, subscription: Subscription) {
        val dataMap = mapOf(
            "name" to subscription.name.name,
            "image" to subscription.image.image.toString(),
            "cost" to subscription.cost.cost,
            "typecost" to subscription.cost.type.toString(),
            "reminder" to subscription.reminder.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "renewalDate" to subscription.renewalDate.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            "userId" to userId
        )
        appwriteService.edit(databaseId, collectionId, subscriptionId, dataMap)
    }

    suspend fun deleteSubscription(subscription: Subscription) {
        if (!(isDefaultImageUrl(subscription.image.image))) {
            appwriteService.deleteStorage(subscription.image.image)
        }
        appwriteService.delete(databaseId, collectionId, subscription.ID)
    }

    suspend fun getSubscription(subscriptionId: String): Subscription? {
        return appwriteService.get(databaseId, collectionId, subscriptionId, Subscription::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllSubscriptions(userId: String): List<Subscription> {
        val query = listOf(Query.equal("userId", userId))
        val queryStrings = query.map { it.toString() }
        val allSubscriptions = appwriteService.getAll(databaseId, collectionId, queryStrings)

        return allSubscriptions.map { data ->
            val name = data["name"] as? String ?: ""
            val image = data["image"] as? String ?: ""
            val cost = when (val costValue = data["cost"]) {
                is Number -> costValue.toDouble()
                else -> 0.0
            }
            val typecost = data["typecost"] as? String ?: ""
            val reminder = data["reminder"] as? String ?: ""
            val renewalDate = data["renewalDate"] as? String ?: ""
            val id = data["\$id"] as? String ?: ""

            Subscription(
                name = SubscriptionName(name),
                image = SubscriptionImage(image),
                cost = SubscriptionCost(cost, SubscriptionCostType.valueOf(typecost)),
                reminder = SubscriptionReminder(LocalDateTime.parse(reminder, DateTimeFormatter.ISO_DATE_TIME)),
                renewalDate = SubscriptionRenewalDate(LocalDateTime.parse(renewalDate, DateTimeFormatter.ISO_DATE_TIME)),
                id
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateSubscriptionRenewalDate(subscriptionId: String, newRenewalDate: LocalDateTime) {
        val formattedRenewalDate = newRenewalDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val dataMap = mapOf(
            "renewalDate" to formattedRenewalDate
        )
        println("Repo: Actualizando renovacion con ID: " + subscriptionId + " a " + formattedRenewalDate)
        appwriteService.edit(databaseId, collectionId, subscriptionId, dataMap)
        println("Repo: Renovación actualizada")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateSubscriptionReminderDate(subscriptionId: String, newReminderDate: LocalDateTime) {
        val formattedReminderDate = newReminderDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val dataMap = mapOf(
            "reminder" to formattedReminderDate
        )
        appwriteService.edit(databaseId, collectionId, subscriptionId, dataMap)
    }


}