package com.example.checkpoint.application.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.application.usecases.usecaseDateTimeCalculator
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import java.time.LocalDate


class serviceDataTimeCalculator : usecaseDateTimeCalculator {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun invoke(costType: SubscriptionCostType, number: Number): LocalDate {
        val today = LocalDate.now()

        return when (costType) {
            SubscriptionCostType.ANNUAL -> today.plusMonths(number.toLong())
            SubscriptionCostType.MONTHLY -> today.plusDays(number.toLong())
        }
    }
}