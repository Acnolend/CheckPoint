package com.example.checkpoint.application.usecases

interface usecaseDeleteSubscription {
    suspend fun invoke(subscriptionId: String)
}