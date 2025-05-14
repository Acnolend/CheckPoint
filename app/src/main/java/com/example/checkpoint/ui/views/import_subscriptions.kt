package com.example.checkpoint.ui.views

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.checkpoint.core.backend.api.appwrite.GoogleService
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.application.services.scheduleReminder
import com.example.checkpoint.application.services.scheduleRenewal
import com.example.checkpoint.application.services.selectDefaultImageUrl
import com.example.checkpoint.application.services.serviceCreateSubscription
import com.example.checkpoint.application.usecases.usecaseCreateSubscription
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.api.response.SubscriptionEditableState
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionCost
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionImage
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionName
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionReminder
import com.example.checkpoint.core.backend.domain.valueobjects.SubscriptionRenewalDate
import com.example.checkpoint.core.store.SubscriptionStore
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.SubscriptionItem
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ImportSubscriptionsView(navController: NavController) {
    val subscriptions = remember { mutableStateListOf<SubscriptionEditableState>() }
    val errorMessages = remember { mutableStateOf<Map<Int, List<String>>>(emptyMap()) }
    val isLoading = remember { mutableStateOf(true) }
    val loadingProgress = remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    val context: Context = LocalContext.current
    val authService = AuthService(context)
    val appwriteService = AppwriteService(context)
    val subscriptionRepository = SubscriptionRepository(appwriteService)
    val createSubscriptionUseCase: usecaseCreateSubscription = serviceCreateSubscription(subscriptionRepository)

    LaunchedEffect(Unit) {
        val service = GoogleService()
        val token = authService.getValidAccessToken(context)

        val totalSteps = 100
        for (step in 1..totalSteps) {
            delay(950)
            loadingProgress.floatValue = step / totalSteps.toFloat()
        }

        val fetchedSubscriptions = withContext(Dispatchers.IO) {
            service.getInboxSubscriptions(token!!)
        }

        val cleanedJson = fetchedSubscriptions.trimIndent().replace("\n", "").replace("\r", "")
            .replace("```json", "")
            .replace("```", "")

        Log.d("FetchedSubscriptions", cleanedJson)

        try {
            val gson = GsonBuilder().setLenient().create()
            val jsonArray = gson.fromJson(cleanedJson, JsonArray::class.java)

            subscriptions.clear()
            subscriptions.addAll(jsonArray.map { item ->
                val obj = item.asJsonObject

                val renewalDate = try {
                    val dateElement = obj["subscriptionRenewalDate"]
                    if (dateElement != null && !dateElement.isJsonNull) {
                        LocalDate.parse(dateElement.asString)
                    } else {
                        LocalDate.now()
                    }
                } catch (e: Exception) {
                    Log.e("JsonParseError", "Error al parsear la fecha", e)
                    LocalDate.now()
                }

                val parsedCost = obj["subscriptionCost"]?.let { prim ->
                    when {
                        prim.isJsonNull -> null
                        prim.asString.isBlank() -> null
                        prim.isJsonPrimitive && prim.asJsonPrimitive.isNumber -> prim.asDouble
                        else -> prim.asString.toDoubleOrNull()
                    }
                }

                val name = obj["subscriptionName"]?.takeIf { !it.isJsonNull }?.asString ?: "Sin nombre"

                val costType = try {
                    val costTypeElement = obj["subscriptionCostType"]
                    if (costTypeElement != null && !costTypeElement.isJsonNull) {
                        SubscriptionCostType.valueOf(costTypeElement.asString)
                    } else {
                        SubscriptionCostType.MONTHLY
                    }
                } catch (e: Exception) {
                    Log.e("JsonParseError", "Error al parsear el tipo de costo", e)
                    SubscriptionCostType.MONTHLY
                }

                SubscriptionEditableState(
                    name = name,
                    renewalDate = renewalDate,
                    cost = parsedCost ?: 0.0,
                    costType = costType
                )
            })
        } catch (e: JsonSyntaxException) {
            Log.e("JsonParseError", "Error al parsear el JSON: ${e.message}")
        }
        isLoading.value = false
    }

    if (isLoading.value) {
        RetroLoadingScreen(loadingProgress.floatValue)
    } else {
        OwnScaffold(navController, content = { modifier ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                PixelArtText(
                    text = context.getString(R.string.detected_subscriptions),
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )

                if (subscriptions.isNotEmpty()) {
                    subscriptions.forEachIndexed { index, sub ->
                        SubscriptionItem(
                            subscriptionName = sub.name,
                            onNameChange = { newName ->
                                subscriptions[index] = sub.copy(name = newName)
                            },
                            subscriptionRenewalDate = sub.renewalDate,
                            onDateChange = { newDate ->
                                subscriptions[index] = sub.copy(renewalDate = newDate.toLocalDate())
                            },
                            subscriptionCost = sub.cost,
                            onCostChange = { newCost ->
                                val costValue = newCost.toDoubleOrNull() ?: 0.0
                                subscriptions[index] = sub.copy(cost = costValue)
                            },
                            subscriptionCostType = sub.costType,
                            onCostTypeChange = { newType ->
                                subscriptions[index] = sub.copy(costType = newType)
                            },
                            isSelected = sub.isSelected,
                            onSelectedChange = { selected ->
                                subscriptions[index] = sub.copy(isSelected = selected)
                            },
                            onErrorChange = { nameError, costError, typeError ->
                                val newErrors = mutableListOf<String>()
                                nameError?.let { newErrors.add(it) }
                                costError?.let { newErrors.add(it) }
                                typeError?.let { newErrors.add(it) }
                                errorMessages.value = errorMessages.value.toMutableMap().apply {
                                    if (sub.isSelected) {
                                        put(index, newErrors)
                                    } else {
                                        remove(index)
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }


                    PixelArtButton(
                        text = context.getString(R.string.import_selected),
                        onClick = {
                            loadingProgress.floatValue = 0f
                            isLoading.value       = true

                            coroutineScope.launch {
                                val totalSubs = subscriptions.size.toFloat()
                                var imported = 0
                                val userId = authService.getUserIdActual().toString()
                                    .substringAfter("(").substringBefore(")")

                                subscriptions.filter { it.isSelected }.forEach { sub ->
                                    val imageUrl = selectDefaultImageUrl(sub.name)
                                    val renewalDate = if (sub.costType == SubscriptionCostType.DAILY) {
                                        LocalDateTime.now().plusDays(1)
                                    } else {
                                        sub.renewalDate.atStartOfDay()
                                    }

                                    val reminderDate = when (sub.costType) {
                                        SubscriptionCostType.DAILY -> sub.renewalDate
                                        else -> sub.renewalDate.minusDays(2)
                                    }.atStartOfDay()

                                    val realCost = sub.cost
                                    val newSubscription = Subscription(
                                        SubscriptionName(sub.name),
                                        SubscriptionImage(imageUrl),
                                        SubscriptionCost(realCost ?: 0.0, sub.costType),
                                        SubscriptionReminder(reminderDate),
                                        SubscriptionRenewalDate(renewalDate),
                                        UUID.randomUUID().toString().replace("-", "")
                                    )

                                    createSubscriptionUseCase.invoke(newSubscription, userId)
                                    SubscriptionStore.addOrUpdateSubscription(newSubscription)
                                    scheduleRenewal(context, newSubscription, true)
                                    scheduleReminder(context, newSubscription, true)

                                    imported++
                                    loadingProgress.floatValue = imported / totalSubs
                                }
                                delay(200)

                                isLoading.value = false
                                navController.navigate("home")
                            }
                        },
                        fontSize = 24.sp,
                        errorMessages = errorMessages.value.flatMap { it.value }.toList(),
                        requiredFields = emptyList()
                    )
                } else {
                    PixelArtText(
                        text = context.getString(R.string.no_subscriptions_detected),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    )
                }
            }
        })
    }
}