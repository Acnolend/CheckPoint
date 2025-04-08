package com.example.checkpoint.application.usecases

import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder

interface usecaseCreateSubscription {
    suspend fun invoke(image: SubscriptionImage, name: SubscriptionName, cost: SubscriptionCost, reminder: SubscriptionReminder, userId: String, subscriptionId: String)
}