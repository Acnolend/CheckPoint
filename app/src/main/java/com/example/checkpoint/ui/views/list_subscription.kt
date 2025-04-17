package com.example.checkpoint.ui.views



import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.application.services.serviceDeleteSubscription
import com.example.checkpoint.application.usecases.usecaseDeleteSubscription
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.store.SubscriptionStore
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.SubscriptionRead
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ListSubscription(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context: Context = LocalContext.current
    val appwriteService = AppwriteService(context)
    val subscriptionRepository = SubscriptionRepository(appwriteService)
    val deleteSubscriptionUseCase: usecaseDeleteSubscription = serviceDeleteSubscription(subscriptionRepository)

    val subscriptions = SubscriptionStore.subscriptions.collectAsState().value

    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtText(
                    text = "SUSCRIPCIONES ACTIVAS",
                    fontSize = 28.sp,
                    color = Color(0xFFE64CF0),
                    fontWeight = FontWeight.Bold
                )
                subscriptions.forEach { subscription ->
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
                            coroutineScope.launch {
                                SubscriptionStore.currentSubscription = subscription
                                deleteSubscriptionUseCase.invoke(subscription)
                                SubscriptionStore.deleteSubscription(subscription.ID)
                            }
                        }
                    )
                }

            }
        }
    )
}