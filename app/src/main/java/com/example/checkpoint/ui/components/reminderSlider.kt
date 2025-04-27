package com.example.checkpoint.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.checkpoint.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReminderSlider(
    isRenewalDateSet: Boolean,
    isSubscriptionTypeSet: Boolean,
    reminderValue: Float,
    onValueChange: (Float) -> Unit,
    renewalDate: LocalDate?
) {
    val isSliderEnabled = isRenewalDateSet && isSubscriptionTypeSet
    val context = LocalContext.current

    val reminderState = remember { mutableStateOf(reminderValue) }

    LaunchedEffect(renewalDate) {
        reminderState.value = 1f
    }

    val daysUntilRenewal = renewalDate?.let {
        ChronoUnit.DAYS.between(LocalDate.now(), it)
    } ?: 0L

    var maxSliderValue = when {
        daysUntilRenewal in 0..1 -> 1f
        daysUntilRenewal in 2..2 -> 2f
        daysUntilRenewal > 2 -> 3f
        else -> 1f
    }

    maxSliderValue = (maxSliderValue - 1).coerceAtLeast(1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        PixelArtText(
            text = context.getString(R.string.reminder_days_before, reminderValue.toInt()),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Slider(
            value = reminderValue,
            onValueChange = { newValue ->
                if (isSliderEnabled) {
                    reminderState.value = newValue
                    onValueChange(newValue)
                }
            },
            enabled = isSliderEnabled,
            valueRange = 1f..maxSliderValue,
            modifier = Modifier.padding(horizontal = 56.dp)
        )
    }
}


