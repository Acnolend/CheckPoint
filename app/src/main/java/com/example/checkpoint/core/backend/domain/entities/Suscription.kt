package com.example.checkpoint.core.backend.domain.entities

import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName

data class Subscription(
    val name: SubscriptionName,
    val image: SubscriptionImage,
    val cost: SubscriptionCost,
    val reminder: SubscriptionReminder,
    val ID: String
)