package com.example.checkpoint.core.store

import com.example.checkpoint.core.backend.domain.entities.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SubscriptionStore {
    var currentSubscription: Subscription? = null

    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions

    fun setSubscriptions(newSubscriptions: List<Subscription>) {
        _subscriptions.value = newSubscriptions
    }

    fun addOrUpdateSubscription(newSubscription: Subscription) {
        val existingSubscriptionIndex = _subscriptions.value.indexOfFirst { it.ID == newSubscription.ID }

        if (existingSubscriptionIndex >= 0) {
            val updatedList = _subscriptions.value.toMutableList()
            updatedList[existingSubscriptionIndex] = newSubscription
            _subscriptions.value = updatedList
        } else {
            val updatedList = _subscriptions.value.toMutableList()
            updatedList.add(newSubscription)
            _subscriptions.value = updatedList
        }
    }

    fun deleteSubscription(subscriptionId: String) {
        val updatedList = _subscriptions.value.filter { it.ID != subscriptionId }
        _subscriptions.value = updatedList
    }
}



