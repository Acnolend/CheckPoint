package com.example.checkpoint.application.usecases

import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import java.time.LocalDate

interface usecaseDateTimeCalculator {
    suspend fun invoke(costType: SubscriptionCostType, number: Number): LocalDate
}