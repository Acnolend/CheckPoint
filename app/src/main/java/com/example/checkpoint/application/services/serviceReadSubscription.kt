package com.example.checkpoint.application.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.application.usecases.usecaseReadSubscription
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription

class serviceReadSubscription(private val subscriptionRepository: SubscriptionRepository) : usecaseReadSubscription {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchByAll(userId: String): List<Subscription> {
        return subscriptionRepository.getAllSubscriptions(userId)
    }

    override suspend fun fetch(subscriptionId: String): Subscription? {
        return subscriptionRepository.getSubscription(subscriptionId)
    }
}