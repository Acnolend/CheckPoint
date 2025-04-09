package com.example.checkpoint.application.usecases

import com.example.checkpoint.core.backend.domain.entities.Subscription

interface usecaseEditSubscription {
    suspend fun invoke(subscription: Subscription, userId: String)
}