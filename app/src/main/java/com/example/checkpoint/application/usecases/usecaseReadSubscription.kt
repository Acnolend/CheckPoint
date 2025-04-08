package com.example.checkpoint.application.usecases

import com.example.checkpoint.core.backend.domain.entities.Subscription

interface usecaseReadSubscription {
    suspend fun fetchByAll(userId: String) : List<Subscription>

    suspend fun fetch(subscriptionId: String) : Subscription?
}