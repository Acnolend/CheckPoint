package com.example.checkpoint.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.ui.views.data_model.validateSubscriptionCostInput
import com.example.checkpoint.ui.views.data_model.validateSubscriptionNameInput
import com.example.checkpoint.ui.views.data_model.validateSubscriptionRenewalDateInput
import java.time.LocalDate
import java.time.LocalDateTime

@SuppressLint("DiscouragedApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SubscriptionItem(
    subscriptionName: String,
    onNameChange: (String) -> Unit,
    subscriptionRenewalDate: LocalDate,
    onDateChange: (LocalDateTime) -> Unit,
    subscriptionCost: Double?,
    onCostChange: (String) -> Unit,
    subscriptionCostType: SubscriptionCostType,
    onCostTypeChange: (SubscriptionCostType) -> Unit,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    onErrorChange: (String?, String?, String?) -> Unit
) {

    val context: Context = LocalContext.current
    var subscriptionNameError by remember { mutableStateOf<String?>(null) }
    var subscriptionCostError by remember { mutableStateOf<String?>(null) }
    var subscriptionRenewalDateError by remember { mutableStateOf<String?>(null) }

    fun validateFields() {
        subscriptionNameError = validateSubscriptionNameInput(subscriptionName)
        subscriptionCostError = subscriptionCost?.let { validateSubscriptionCostInput(it, subscriptionCostType.name) }
        subscriptionRenewalDateError = validateSubscriptionRenewalDateInput(subscriptionRenewalDate.atStartOfDay())

        onErrorChange(subscriptionNameError, subscriptionCostError, subscriptionRenewalDateError)
    }

    validateFields()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF2D006C))
            .padding(16.dp)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectedChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        PixelArtTextField(
            label = context.getString(R.string.subscription_name),
            text = subscriptionName,
            onTextChange = {
                onNameChange(it)
                validateFields()
            },
            isError = subscriptionNameError != null,
            errorMessage = subscriptionNameError?.let {
                context.getString(context.resources.getIdentifier(it, "string", context.packageName))
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PixelArtTextField(
            label = context.getString(R.string.subscription_cost),
            text = subscriptionCost?.toString() ?: "",
            onTextChange = { newText ->
                onCostChange(newText)
                validateFields()
            },
            isError = subscriptionCostError != null,
            errorMessage = subscriptionCostError?.let {
                context.getString(context.resources.getIdentifier(it, "string", context.packageName))
            },
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(8.dp))

        SubscriptionFilterDropdown(
            selectedType = subscriptionCostType,
            onTypeSelected = { it?.let { onCostTypeChange(it) } },
            showAllOption = false
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (subscriptionCostType != SubscriptionCostType.DAILY) {
            DatePickerField(selectedDate = subscriptionRenewalDate.atStartOfDay(),
                onDateSelected = { date ->
                    onDateChange(date)
                    validateFields()
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (subscriptionRenewalDateError != null) {
            PixelArtText(
                context.getString(context.resources.getIdentifier(subscriptionRenewalDateError!!, "string", context.packageName)),
                color = Color.Red
            )
        }
    }
}

