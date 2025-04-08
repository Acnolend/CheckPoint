package com.example.checkpoint.core.store

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.checkpoint.core.backend.domain.entities.Subscription

object SubscriptionStore {
    var currentSubscription: Subscription? = null
    var subscriptions: List<Subscription> by mutableStateOf(emptyList())
}