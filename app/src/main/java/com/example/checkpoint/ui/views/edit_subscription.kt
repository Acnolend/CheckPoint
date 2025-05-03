package com.example.checkpoint.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
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
import com.example.checkpoint.R
import com.example.checkpoint.application.services.scheduleReminder
import com.example.checkpoint.application.services.scheduleRenewal
import com.example.checkpoint.application.services.selectDefaultImageUrl
import com.example.checkpoint.application.services.serviceEditSubscription
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
import com.example.checkpoint.ui.components.ReminderSlider
import com.example.checkpoint.ui.components.SubscriptionFilterDropdown
import com.example.checkpoint.ui.views.data_model.validateSubscriptionCostInput
import com.example.checkpoint.ui.views.data_model.validateSubscriptionNameInput
import com.example.checkpoint.ui.views.data_model.validateSubscriptionRenewalDateInput
import kotlinx.coroutines.launch
import java.time.LocalDateTime


@SuppressLint("DiscouragedApi")
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
    var selectedType by remember { mutableStateOf(SubscriptionCostType.MONTHLY) }
    var reminderDays by remember { mutableFloatStateOf(1f) }
    var renewalDate by remember { mutableStateOf(subscription!!.renewalDate.dateTime) }

    var subscriptionNameError by remember { mutableStateOf<String?>(null) }
    var subscriptionCostError by remember { mutableStateOf<String?>(null) }
    var subscriptionRenewalDateError by remember { mutableStateOf<String?>(null) }
    val subscriptionReminderDateError by remember { mutableStateOf<String?>(null) }

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
                    text = context.getString(R.string.edit_subscription),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE64CF0)
                )
                Spacer(modifier = Modifier.height(32.dp))
                ImageSubscription(onImageSelected, subscription!!.image.image)
                Spacer(modifier = Modifier.height(32.dp))
                PixelArtTextField(
                    context.getString(R.string.subscription_name),
                    name,
                    onTextChange = {
                        name = it
                        subscriptionNameError = validateSubscriptionNameInput(it)
                    },
                    isError = subscriptionNameError != null,
                    errorMessage = subscriptionNameError?.let { context.getString(context.resources.getIdentifier(it, "string", context.packageName)) }
                )
                Column(
                    modifier = Modifier
                        .widthIn(max = 400.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    PixelArtTextField(
                        context.getString(R.string.subscription_cost),
                        cost,
                        onTextChange = {
                            cost = it
                            val newCost = cost.toDoubleOrNull()
                            subscriptionCostError =
                                newCost?.let { it1 -> validateSubscriptionCostInput(it1,
                                    selectedType.name
                                ) }
                        },
                        isError = subscriptionCostError != null,
                        errorMessage = subscriptionCostError?.let { context.getString(context.resources.getIdentifier(it, "string", context.packageName)) },
                        keyboardType = KeyboardType.Number
                    )
                    SubscriptionFilterDropdown(
                        selectedType = selectedType,
                        onTypeSelected = { type -> selectedType = type!! },
                        false
                    )

                }
                Spacer(modifier = Modifier.height(24.dp))
                if(selectedType != SubscriptionCostType.DAILY) {
                    DatePickerField(
                        selectedDate = renewalDate,
                        onDateSelected = { date ->
                            renewalDate = date
                            subscriptionRenewalDateError = validateSubscriptionRenewalDateInput(date)
                        }
                    )
                    if (subscriptionRenewalDateError != null) {
                        PixelArtText(
                            context.getString(context.resources.getIdentifier(subscriptionRenewalDateError!!, "string", context.packageName)),
                            color = Color.Red
                        )
                    }
                }
                if (selectedType != SubscriptionCostType.DAILY) {
                    ReminderSlider(
                        isRenewalDateSet = true,
                        isSubscriptionTypeSet = name.isNotEmpty() && cost.isNotEmpty(),
                        reminderValue = reminderDays,
                        onValueChange = { newValue ->
                            reminderDays = newValue
                        },
                        renewalDate.toLocalDate()
                    )
                }
                PixelArtButton(
                    text = context.getString(R.string.save),
                    onClick = {
                        coroutineScope.launch {
                            val costValue = cost.toDoubleOrNull() ?: return@launch
                            val type = selectedType
                            val imageUrl = imageUri?.let { uri ->
                                appwriteService.uploadImageToAppwrite(context, uri)
                            } ?: selectDefaultImageUrl(name)

                            if (selectedType == SubscriptionCostType.DAILY) {
                                renewalDate = LocalDateTime.now().plusDays(1)
                            }

                            val reminderDate = renewalDate.let {
                                when (selectedType) {
                                    SubscriptionCostType.DAILY -> it.toLocalDate()
                                    else -> it.toLocalDate().minusDays(reminderDays.toLong())
                                }
                            }



                            val userId = authService.getUserIdActual().toString().substringAfter("(").substringBefore(")")
                            val newSubscription = Subscription(
                                SubscriptionName(name),
                                SubscriptionImage(imageUrl),
                                SubscriptionCost(costValue, type),
                                SubscriptionReminder(reminderDate!!.atStartOfDay()),
                                SubscriptionRenewalDate(renewalDate),
                                subscription.ID
                            )
                            editSubscriptionUseCase.invoke(newSubscription, userId)
                            SubscriptionStore.addOrUpdateSubscription(newSubscription)
                            scheduleRenewal(context, newSubscription)
                            scheduleReminder(context, newSubscription)
                            navController.navigate("home")
                        }
                    },
                    fontSize = 24.sp,
                    errorMessages = listOf(subscriptionNameError.orEmpty(), subscriptionCostError.orEmpty(), subscriptionRenewalDateError.orEmpty(), subscriptionReminderDateError.orEmpty()),
                    requiredFields = listOf(imageUri.toString(), name, cost, reminderDays.toString(),
                        renewalDate.toString()
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    )
}