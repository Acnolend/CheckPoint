package com.example.checkpoint.core.backend.api.firestore

import com.example.checkpoint.core.backend.domain.entities.Subscription

class SubscriptionRepository(private val firestoreService: FirestoreService) {

    suspend fun saveSubscription(userId: String, subscription: Subscription) {
        firestoreService.save("users/$userId/subscriptions", subscription.name.name, subscription)
    }

    suspend fun getSubscription(userId: String, subscriptionId: String): Subscription? {
        return firestoreService.get("users/$userId/subscriptions", subscriptionId, Subscription::class.java)
    }

    suspend fun getAllSubscriptions(userId: String): List<Subscription> {
        return firestoreService.getAll("users", userId, Subscription::class.java)
    }
}