package com.example.checkpoint.application.usecases

import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder

interface usecaseEditSubscription {
    suspend fun invoke(subscriptionId: String, image: SubscriptionImage, name: SubscriptionName, cost: SubscriptionCost, reminder: SubscriptionReminder, userId: String)
}