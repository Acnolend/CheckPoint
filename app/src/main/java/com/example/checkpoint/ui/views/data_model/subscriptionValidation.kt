package com.example.checkpoint.ui.views.data_model

import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionRenewalDate
import java.time.LocalDateTime


fun validateSubscriptionNameInput(input: String): String? {
    return try {
        SubscriptionName(input)
        null
    } catch (e: IllegalArgumentException) {
        e.message
    }
}

fun validateSubscriptionCostInput(cost: Double, type: String): String? {
    return try {
        val subscriptionCostType = SubscriptionCostType.valueOf(type.uppercase())
        SubscriptionCost(cost, subscriptionCostType)
        null
    } catch (e: IllegalArgumentException) {
        e.message
    }
}

fun validateSubscriptionReminderInput(input: LocalDateTime): String? {
    return try {
        SubscriptionReminder(input)
        null
    } catch (e: IllegalArgumentException) {
        e.message
    }
}

fun validateSubscriptionRenewalDateInput(input: LocalDateTime): String? {
    return try {
        SubscriptionRenewalDate(input)
        null
    } catch (e: IllegalArgumentException) {
        e.message
    }
}


