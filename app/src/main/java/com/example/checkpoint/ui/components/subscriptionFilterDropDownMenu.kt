package com.example.checkpoint.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType

@Composable
fun SubscriptionFilterDropdown(
    selectedType: SubscriptionCostType?,
    onTypeSelected: (SubscriptionCostType?) -> Unit,
    showAllOption: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box {
        PixelArtButton(
            text = when {
                selectedType != null -> context.getString(when (selectedType) {
                    SubscriptionCostType.DAILY -> R.string.daily
                    SubscriptionCostType.WEEKLY -> R.string.weekly
                    SubscriptionCostType.BIWEEKLY -> R.string.biweekly
                    SubscriptionCostType.MONTHLY -> R.string.monthly
                    SubscriptionCostType.BIMONTHLY -> R.string.bimonthly
                    SubscriptionCostType.QUARTERLY -> R.string.quarterly
                    SubscriptionCostType.SEMIANNUAL -> R.string.semiannual
                    SubscriptionCostType.ANNUAL -> R.string.annual
                })
                showAllOption -> context.getString(R.string.all)  // Si showAllOption es true, mostrar "All"
                else -> context.getString(R.string.select_subscription_type)  // Si showAllOption es false y no se ha seleccionado tipo
            },
            onClick = { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (showAllOption) {
                DropdownMenuItem(
                    text = { PixelArtText(context.getString(R.string.all)) },
                    onClick = {
                        onTypeSelected(null)
                        expanded = false
                    }
                )
            }
            SubscriptionCostType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { PixelArtText(context.getString(
                        when (type) {
                            SubscriptionCostType.DAILY -> R.string.daily
                            SubscriptionCostType.WEEKLY -> R.string.weekly
                            SubscriptionCostType.BIWEEKLY -> R.string.biweekly
                            SubscriptionCostType.MONTHLY -> R.string.monthly
                            SubscriptionCostType.BIMONTHLY -> R.string.bimonthly
                            SubscriptionCostType.QUARTERLY -> R.string.quarterly
                            SubscriptionCostType.SEMIANNUAL -> R.string.semiannual
                            SubscriptionCostType.ANNUAL -> R.string.annual
                        }
                    )) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}
