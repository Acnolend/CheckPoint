package com.example.checkpoint.application.usecases

import com.example.checkpoint.core.backend.domain.entities.Subscription

interface usecaseDeleteSubscription {
    suspend fun invoke(subscription: Subscription)
}