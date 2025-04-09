package com.example.checkpoint.application.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.application.usecases.usecaseCreateSubscription
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder

class serviceCreateSubscription (
    private val subscriptionRepository: SubscriptionRepository
) : usecaseCreateSubscription {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun invoke(subscription: Subscription, userId: String) {
        subscriptionRepository.saveSubscription(userId, subscription, subscription.ID)
    }
}