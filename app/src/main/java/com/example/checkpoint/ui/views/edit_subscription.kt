package com.example.checkpoint.ui.views

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.ui.components.ImageSubscription
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.PixelArtTextField
import androidx.compose.ui.text.input.KeyboardType
import com.example.checkpoint.application.services.serviceDataTimeCalculator
import com.example.checkpoint.application.services.serviceEditSubscription
import com.example.checkpoint.application.usecases.usecaseDateTimeCalculator
import com.example.checkpoint.application.usecases.usecaseEditSubscription
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionRenewalDate
import com.example.checkpoint.core.store.SubscriptionStore
import com.example.checkpoint.ui.components.DatePickerField
import com.example.checkpoint.ui.views.data_model.validateSubscriptionCostInput
import com.example.checkpoint.ui.views.data_model.validateSubscriptionNameInput
import com.example.checkpoint.ui.views.data_model.validateSubscriptionReminderInput
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditSubscription(navController: NavController) {
    val subscription = SubscriptionStore.currentSubscription
    val context: Context = LocalContext.current

    val appwriteService = AppwriteService(context)
    val authService = AuthService(context)
    val subscriptionRepository = SubscriptionRepository(appwriteService)
    val editSubscriptionUseCase: usecaseEditSubscription = serviceEditSubscription(subscriptionRepository)
    val coroutineScope = rememberCoroutineScope()


    var name by remember { mutableStateOf(subscription!!.name.name) }
    var cost by remember { mutableStateOf(subscription?.cost!!.cost.toString()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var subscriptionCostType by remember { mutableStateOf(SubscriptionCostType.MONTHLY) }
    var reminderDays by remember { mutableFloatStateOf(1f) }
    var renewalDate by remember { mutableStateOf(subscription!!.renewalDate.dateTime) }

    var subscriptionNameError by remember { mutableStateOf<String?>(null) }
    var subscriptionCostError by remember { mutableStateOf<String?>(null) }
    var subscriptionRenewalDateError by remember { mutableStateOf<String?>(null) }
    var subscriptionReminderDateError by remember { mutableStateOf<String?>(null) }

    val normalizedReminderValue = remember(reminderDays, subscriptionCostType) {
        if (subscriptionCostType == SubscriptionCostType.MONTHLY) {
            reminderDays.coerceIn(1f..30f)
        } else {
            (reminderDays / 30f).coerceIn(1f..12f)
        }
    }
    val onImageSelected: (Uri) -> Unit = { uri ->
        imageUri = uri
    }
    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtText(
                    text = "EDITAR SUSCRIPCIÓN",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE64CF0)
                )
                Spacer(modifier = Modifier.height(32.dp))
                ImageSubscription(onImageSelected, subscription!!.image.image)
                Spacer(modifier = Modifier.height(32.dp))
                PixelArtTextField(
                    "NOMBRE",
                    name,
                    onTextChange = {
                        name = it
                        subscriptionNameError = validateSubscriptionNameInput(it)
                    },
                    isError = subscriptionNameError != null,
                    errorMessage = subscriptionNameError
                )
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .padding(start = 56.dp, end = 56.dp)
                        .fillMaxWidth(),
                ) {
                    PixelArtTextField(
                        "COSTE",
                        cost,
                        onTextChange = {
                            cost = it
                            val newCost = cost.toDoubleOrNull()
                            subscriptionCostError =
                                newCost?.let { it1 -> validateSubscriptionCostInput(it1, if (subscriptionCostType == SubscriptionCostType.MONTHLY) "MONTHLY" else "ANNUAL") }
                        },
                        isError = subscriptionCostError != null,
                        errorMessage = subscriptionCostError,
                        keyboardType = KeyboardType.Number
                    )
                    PixelArtButton(
                        text = if (subscriptionCostType == SubscriptionCostType.MONTHLY) "Mensual" else "Anual",
                        onClick = {
                            if (subscriptionCostType == SubscriptionCostType.ANNUAL && reminderDays > 0) {
                                reminderDays = 30f
                            }
                            subscriptionCostType = if (subscriptionCostType == SubscriptionCostType.MONTHLY)
                                SubscriptionCostType.ANNUAL
                            else
                                SubscriptionCostType.MONTHLY
                        },
                        modifier = Modifier.align(Alignment.End).padding(top = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                DatePickerField { date ->
                    renewalDate = date
                }
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtText(if (subscriptionCostType == SubscriptionCostType.MONTHLY) "RECORDAR CADA ${normalizedReminderValue.toInt()} DÍAS" else "RECORDAR CADA ${normalizedReminderValue.toInt()} MESES")
                Slider(
                    value = normalizedReminderValue,
                    onValueChange = { newValue ->
                        val proposedReminderDays = if (subscriptionCostType == SubscriptionCostType.MONTHLY) {
                            newValue.coerceIn(1f..30f)
                        } else {
                            (newValue * 30f).coerceIn(1f..360f)
                        }
                        if (renewalDate != null) {
                            val reminderDateCandidate = renewalDate!!.toLocalDate()
                                .minusDays(proposedReminderDays.toLong())
                                .atStartOfDay()

                            subscriptionReminderDateError = validateSubscriptionReminderInput(reminderDateCandidate)
                        }

                        reminderDays = proposedReminderDays
                    },
                    valueRange = if (subscriptionCostType == SubscriptionCostType.MONTHLY) 1f..30f else 1f..12f,
                    steps = if (subscriptionCostType == SubscriptionCostType.MONTHLY) 29 else 11,
                    modifier = Modifier.padding(start = 56.dp, end = 56.dp)
                )
                PixelArtButton(
                    text = "GUARDAR",
                    onClick = {
                        coroutineScope.launch {
                            val costValue = cost.toDoubleOrNull() ?: return@launch
                            val type = subscriptionCostType
                            val dataTimeCalculator: usecaseDateTimeCalculator = serviceDataTimeCalculator()
                            val dateTime = dataTimeCalculator.invoke(type, normalizedReminderValue)
                            val localDateTime = dateTime.atStartOfDay()

                            val imageUrl = imageUri?.let { appwriteService.uploadImageToAppwrite(context, it) } ?: subscription.image.image

                            val userId = authService.getUserIdActual().toString().substringAfter("(").substringBefore(")")
                            val newSubscription = Subscription(
                                SubscriptionName(name),
                                SubscriptionImage(imageUrl),
                                SubscriptionCost(costValue, type),
                                SubscriptionReminder(localDateTime),
                                SubscriptionRenewalDate(renewalDate),
                                subscription.ID
                            )
                            editSubscriptionUseCase.invoke(newSubscription, userId)
                            SubscriptionStore.addOrUpdateSubscription(newSubscription)
                            navController.navigate("home")
                        }
                    },
                    fontSize = 24.sp,
                    errorMessages = listOf(subscriptionNameError.orEmpty(), subscriptionCostError.orEmpty(), subscriptionRenewalDateError.orEmpty(), subscriptionReminderDateError.orEmpty()),
                    requiredFields = listOf(imageUri.toString(), name, cost, reminderDays.toString(), renewalDate?.toString() ?: "")
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}