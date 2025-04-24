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
        when (e.message) {
            SubscriptionName.ERROR_EMPTY -> "subscription_name_error_empty"
            SubscriptionName.ERROR_MAX_LENGTH -> "subscription_name_error_maxLength"
            SubscriptionName.ERROR_MIN_LENGTH -> "subscription_name_error_minLength"
            SubscriptionName.ERROR_WRONG_FORMAT -> "subscription_name_error_wrongFormat"
            else -> null
        }
    }
}

fun validateSubscriptionCostInput(cost: Double, type: String): String? {
    return try {
        val subscriptionCostType = SubscriptionCostType.valueOf(type.uppercase())
        SubscriptionCost(cost, subscriptionCostType)
        null
    } catch (e: IllegalArgumentException) {
        "subscription_cost_error"
    }
}

fun validateSubscriptionReminderInput(input: LocalDateTime): String? {
    return try {
        SubscriptionReminder(input)
        null
    } catch (e: IllegalArgumentException) {
        "subscription_reminder_error"
    }
}

fun validateSubscriptionRenewalDateInput(input: LocalDateTime): String? {
    return try {
        SubscriptionRenewalDate(input)
        null
    } catch (e: IllegalArgumentException) {
        "subscription_renewalDate_error"
    }
}


