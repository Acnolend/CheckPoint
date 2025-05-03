package com.example.checkpoint.application.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.checkpoint.core.backend.domain.entities.Subscription
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
fun getPredictedCostThisMonth(subscriptions: List<Subscription>): Double {
    val currentMonth = LocalDate.now().month
    val currentYear = LocalDate.now().year
    return subscriptions
        .filter {
            val date = it.renewalDate.dateTime.toLocalDate()
            date.month == currentMonth && date.year == currentYear
        }
        .sumOf { it.cost.cost }
}