package com.example.checkpoint.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.work.WorkManager
import com.example.checkpoint.R
import com.example.checkpoint.application.services.serviceDeleteSubscription
import com.example.checkpoint.application.usecases.usecaseDeleteSubscription
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.store.SubscriptionStore
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtPopup
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.PixelArtTextField
import com.example.checkpoint.ui.components.SubscriptionFilterDropdown
import com.example.checkpoint.ui.components.SubscriptionRead
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ListSubscription(navController: NavController) {

    val searchText = remember { mutableStateOf("") }
    val selectedFilter = remember { mutableStateOf<SubscriptionCostType?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context: Context = LocalContext.current
    val appwriteService = AppwriteService(context)
    val subscriptionRepository = SubscriptionRepository(appwriteService)
    val deleteSubscriptionUseCase: usecaseDeleteSubscription = serviceDeleteSubscription(subscriptionRepository)
    val today = LocalDate.now()

    val subscriptions = SubscriptionStore.subscriptions.collectAsState().value

    val filteredSubscriptions = subscriptions.filter { subscription ->
        val matchesSearch = subscription.name.name.contains(searchText.value, ignoreCase = true)
        val matchesFilter = selectedFilter.value?.let {
            subscription.cost.type == it
        } ?: true

        matchesSearch && matchesFilter
    }

    var subscriptionToDelete by remember { mutableStateOf<Subscription?>(null) }

    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtText(
                    text = context.getString(R.string.active_subs),
                    fontSize = 28.sp,
                    color = Color(0xFFE64CF0),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PixelArtTextField(
                        label = context.getString(R.string.search),
                        text = searchText.value,
                        onTextChange = { searchText.value = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PixelArtText(
                            text = context.getString(R.string.select_subscription_type)+":",
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        SubscriptionFilterDropdown(
                            selectedType = selectedFilter.value,
                            onTypeSelected = { selectedFilter.value = it }
                        )
                    }
                }
                filteredSubscriptions.forEach { subscription ->
                    Spacer(modifier = Modifier.height(24.dp))
                    SubscriptionRead(
                        subscription.name.name,
                        subscription.image.image,
                        subscription.cost.cost.toString(),
                        subscription.cost.type.toString(),
                        onEditClick = {
                            SubscriptionStore.currentSubscription = subscription
                            navController.navigate("edit_subscription")
                        },
                        onDeleteClick = {
                            subscriptionToDelete = subscription
                            showPopup = true
                        },
                        isRenewalSoon = ChronoUnit.DAYS.between(today, subscription.renewalDate.dateTime) in 1..2
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    )

    if (showPopup && subscriptionToDelete != null) {
        PixelArtPopup(
            onDismissRequest = {
                showPopup = false
                subscriptionToDelete = null
            },
            title = context.getString(R.string.delete_subscription_title),
            message = context.getString(R.string.delete_subscription_message, subscriptionToDelete?.name?.name),
            onConfirm = {
                coroutineScope.launch {
                    subscriptionToDelete?.let { sub ->
                        SubscriptionStore.currentSubscription = sub
                        WorkManager.getInstance(context).cancelAllWorkByTag("${sub.ID}_renewal")
                        WorkManager.getInstance(context).cancelAllWorkByTag("${sub.ID}_reminder")
                        deleteSubscriptionUseCase.invoke(sub)
                        SubscriptionStore.deleteSubscription(sub.ID)
                    }
                    showPopup = false
                    subscriptionToDelete = null
                }
            }
        )
    }
}
