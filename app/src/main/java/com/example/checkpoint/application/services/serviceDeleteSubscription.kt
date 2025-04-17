package com.example.checkpoint.application.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.application.usecases.usecaseDeleteSubscription
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription

class serviceDeleteSubscription (
    private val subscriptionRepository: SubscriptionRepository
) : usecaseDeleteSubscription {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun invoke(subscription: Subscription) {
        subscriptionRepository.deleteSubscription(subscription)
    }
}