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
import java.time.Month
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun YearlyBarChart(subscriptions: List<Subscription>) {
    val context: Context = LocalContext.current
    val today = LocalDate.now()
    val year = today.year
    LocalDate.of(year, 12, 31)

    val filteredSubscriptions = subscriptions.filter {
        it.renewalDate.dateTime.toLocalDate().isAfter(today) || it.renewalDate.dateTime.toLocalDate().isEqual(today)
    }

    val months = Month.entries.toTypedArray()

    val monthlyCosts = months.associateWith { month ->
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        (1..daysInMonth).sumOf { day ->
            val date = LocalDate.of(year, month, day)
            filteredSubscriptions.sumOf { subscription ->
                val renewalDate = subscription.renewalDate.dateTime.toLocalDate()
                val cost = subscription.cost.cost
                val type = subscription.cost.type

                val shouldChargeToday = when (type) {
                    SubscriptionCostType.DAILY       -> !date.isBefore(renewalDate)
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
                    SubscriptionCostType.ANNUAL      -> date.month == renewalDate.month && date.dayOfMonth == renewalDate.dayOfMonth && !date.isBefore(renewalDate)
                }
                if (shouldChargeToday) cost else 0.0
            }
        }
    }

    val totalAnnual = monthlyCosts.values.sum()

    Column {
        PixelArtText(
            text = context.getString(R.string.estimated_annual_expenses),
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        BarChart(
            data = monthlyCosts.values.map { it.toFloat() },
            labels = months.map { it.name.take(3) },
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
        PixelArtText(
            text = context.getString(R.string.chart_axis_description),
            modifier = Modifier.padding(top = 12.dp)
        )
        val formattedTotalAnnual = context.getString(R.string.total_annual_estimated) + " " + CurrencyStore.formatPrice(totalAnnual.toString())
        PixelArtText(
            text = formattedTotalAnnual,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}