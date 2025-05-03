package com.example.checkpoint.core.backend.api.response

import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import java.time.LocalDate

data class SubscriptionEditableState(
    var name: String,
    var renewalDate: LocalDate,
    var cost: Double?,
    var costType: SubscriptionCostType,
    var isSelected: Boolean = false
)