package com.example.checkpoint.ui.components

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.store.CurrencyStore
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyBarChart(subscriptions: List<Subscription>) {
    val context: Context = LocalContext.current
    val today = LocalDate.now()
    val daysInMonth = today.lengthOfMonth()

    val dailyCosts = (today.dayOfMonth..daysInMonth).associateWith { day ->
        val date = today.withDayOfMonth(day)

        subscriptions.sumOf { subscription ->
            val renewalDate = subscription.renewalDate.dateTime.toLocalDate()
            val cost = subscription.cost.cost
            val type = subscription.cost.type

            val shouldChargeToday = when (type) {
                SubscriptionCostType.DAILY       -> true
                SubscriptionCostType.WEEKLY      -> ChronoUnit.DAYS.between(renewalDate, date) % 7 == 0L && !date.isBefore(renewalDate)
                SubscriptionCostType.BIWEEKLY    -> ChronoUnit.DAYS.between(renewalDate, date) % 14 == 0L && !date.isBefore(renewalDate)
                SubscriptionCostType.MONTHLY     -> {
                    val expectedDate = date.withDayOfMonth(
                        minOf(renewalDate.dayOfMonth, date.lengthOfMonth())
                    )
                    date == expectedDate && !date.isBefore(renewalDate)
                }
                SubscriptionCostType.BIMONTHLY   -> {
                    val monthsBetween = ChronoUnit.MONTHS.between(renewalDate.withDayOfMonth(1), date.withDayOfMonth(1))
                    monthsBetween % 2 == 0L && date.dayOfMonth == renewalDate.dayOfMonth && !date.isBefore(renewalDate)
                }
                SubscriptionCostType.QUARTERLY   -> {
                    val monthsBetween = ChronoUnit.MONTHS.between(renewalDate.withDayOfMonth(1), date.withDayOfMonth(1))
                    monthsBetween % 3 == 0L && date.dayOfMonth == renewalDate.dayOfMonth && !date.isBefore(renewalDate)
                }
                SubscriptionCostType.SEMIANNUAL  -> {
                    val monthsBetween = ChronoUnit.MONTHS.between(renewalDate.withDayOfMonth(1), date.withDayOfMonth(1))
                    monthsBetween % 6 == 0L && date.dayOfMonth == renewalDate.dayOfMonth && !date.isBefore(renewalDate)
                }
                SubscriptionCostType.ANNUAL      -> date.month == renewalDate.month && date.dayOfMonth == renewalDate.dayOfMonth
            }
            if (shouldChargeToday) cost else 0.0
        }
    }

    val totalCost = dailyCosts.values.sum()

    Column {
        PixelArtText(
            text = context.getString(R.string.upcoming_payments_this_month),
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        BarChart(
            data = dailyCosts.values.map { it.toFloat() },
            labels = dailyCosts.keys.map { it.toString() },
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
        PixelArtText(
            text = context.getString(R.string.chart_axis_description),
            modifier = Modifier.padding(top = 12.dp)
        )
        val formattedTotalEstimated = context.getString(R.string.total_estimated) + " " + CurrencyStore.formatPrice(totalCost.toString())
        PixelArtText(
            text = formattedTotalEstimated,
            modifier = Modifier
                .padding(top = 12.dp)
        )
    }

}