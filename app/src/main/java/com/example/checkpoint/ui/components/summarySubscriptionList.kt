package com.example.checkpoint.ui.components

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.store.CurrencyStore
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun SummarySubscriptionList(subscriptions: List<Subscription>, isMonthlyMode: Boolean) {
    val context: Context = LocalContext.current
    val today = LocalDate.now()
    val daysInMonth = today.lengthOfMonth()
    val endOfYear = LocalDate.of(today.year, 12, 31)

    val expandedList = if (isMonthlyMode) {
        val list = mutableListOf<Pair<Subscription, LocalDate>>()

        (today.dayOfMonth..daysInMonth).forEach { day ->
            val date = today.withDayOfMonth(day)

            subscriptions.forEach { sub ->
                val renewalDate = sub.renewalDate.dateTime.toLocalDate()
                val type = sub.cost.type

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
                    SubscriptionCostType.ANNUAL      -> date.month == renewalDate.month && date.dayOfMonth == renewalDate.dayOfMonth && !date.isBefore(renewalDate)
                }

                if (shouldChargeToday && date.month == today.month && date.year == today.year) {
                    list.add(sub to date)
                }
            }
        }

        list.sortedBy { it.second }
    } else {
        subscriptions.map { it to it.renewalDate.dateTime.toLocalDate() }
            .sortedBy { it.second }
    }

    val groupedByDate = expandedList.groupBy { it.second }
    var totalCost = 0.0
    var count = 0

    Column {
        groupedByDate.forEach { (date, subsOnDate) ->
            PixelArtText(
                text = " - ${date.format(DateTimeFormatter.ofPattern("dd MMMM"))} -",
                modifier = Modifier.padding(vertical = 8.dp)
            )

            subsOnDate.forEach { (sub, _) ->
                val daysUntilEndOfYear = ChronoUnit.DAYS.between(today, endOfYear)

                val (remainingPayments, remainingCost) = when (sub.cost.type) {
                    SubscriptionCostType.DAILY -> {
                        val payments = daysUntilEndOfYear + 1
                        payments to payments * sub.cost.cost
                    }
                    SubscriptionCostType.WEEKLY -> {
                        val payments = (daysUntilEndOfYear / 7) + 1
                        payments to payments * sub.cost.cost
                    }
                    SubscriptionCostType.BIWEEKLY -> {
                        val payments = (daysUntilEndOfYear / 14) + 1
                        payments to payments * sub.cost.cost
                    }
                    SubscriptionCostType.MONTHLY -> {
                        val monthsLeft = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), endOfYear.withDayOfMonth(1)) + 1
                        monthsLeft to monthsLeft * sub.cost.cost
                    }
                    SubscriptionCostType.BIMONTHLY -> {
                        val monthsLeft = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), endOfYear.withDayOfMonth(1)) + 1
                        val payments = (monthsLeft / 2) + if (monthsLeft % 2 != 0L) 1 else 0
                        payments to payments * sub.cost.cost
                    }
                    SubscriptionCostType.QUARTERLY -> {
                        val monthsLeft = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), endOfYear.withDayOfMonth(1)) + 1
                        val payments = (monthsLeft / 3) + if (monthsLeft % 3 != 0L) 1 else 0
                        payments to payments * sub.cost.cost
                    }
                    SubscriptionCostType.SEMIANNUAL -> {
                        val monthsLeft = ChronoUnit.MONTHS.between(today.withDayOfMonth(1), endOfYear.withDayOfMonth(1)) + 1
                        val payments = (monthsLeft / 6) + if (monthsLeft % 6 != 0L) 1 else 0
                        payments to payments * sub.cost.cost
                    }
                    SubscriptionCostType.ANNUAL -> {
                        val payments = if (today <= endOfYear) 1 else 0
                        payments.toLong() to payments * sub.cost.cost
                    }
                }

                totalCost += remainingCost
                count += remainingPayments.toInt()

                Card(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2D006C)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {

                    val formattedCostPerPayment = context.getString(R.string.cost_per_payment) + " " + CurrencyStore.formatPrice(sub.cost.cost.toString())
                    val formattedTotalCostUntilDecember = context.getString(R.string.total_cost_until_december_31) + " " + CurrencyStore.formatPrice(remainingCost.toString())

                    Column(Modifier.padding(12.dp)) {
                        PixelArtText(sub.name.name)
                        PixelArtText(formattedCostPerPayment)
                        if (!isMonthlyMode) {
                            PixelArtText(context.getString(R.string.remaining_payments_this_year, remainingPayments))
                            PixelArtText(formattedTotalCostUntilDecember)
                        }
                    }
                }
            }
        }
    }
}
