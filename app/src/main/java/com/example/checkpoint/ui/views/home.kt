package com.example.checkpoint.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.application.services.getNextRenewalDate
import com.example.checkpoint.application.services.getPredictedCostThisMonth
import com.example.checkpoint.application.services.serviceReadSubscription
import com.example.checkpoint.application.usecases.usecaseReadSubscription
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.store.CurrencyStore
import com.example.checkpoint.core.store.SubscriptionStore
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtText

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(navController: NavController) {
    val context: Context = LocalContext.current
    val appwriteService = AppwriteService(context)
    val authService = AuthService(context)
    val subscriptionRepository = SubscriptionRepository(appwriteService)
    val readSubscriptionUseCase: usecaseReadSubscription = serviceReadSubscription(subscriptionRepository)
    val subscriptions by SubscriptionStore.subscriptions.collectAsState()
    var predictedTotal by remember { mutableDoubleStateOf(0.0) }
    var nextRenewalDate by remember { mutableStateOf<Pair<String, String>?>(null) }


    LaunchedEffect(Unit) {
        val userId = authService.getUserIdActual().toString().substringAfter("(").substringBefore(")")
        val data = readSubscriptionUseCase.fetchByAll(userId)
        SubscriptionStore.setSubscriptions(data)
        predictedTotal = getPredictedCostThisMonth(data)
        nextRenewalDate = getNextRenewalDate(data)
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
                    text = context.getString(R.string.next_expense_this_month),
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                PixelArtText(
                    text = CurrencyStore.formatPrice(predictedTotal.toString()),
                    fontSize = 56.sp,
                    color = Color(0xFFE64CF0)
                )
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(
                    text = context.getString(R.string.next_renewal),
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(48.dp))
                if (nextRenewalDate != null) {
                    val (name, date) = nextRenewalDate!!
                    Box(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_alert_nextrenewal),
                            contentDescription = "Subscription Icon",
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterStart)
                                .padding(start = 4.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PixelArtText(
                                    text = name,
                                    fontSize = 22.sp,
                                    color = Color(0xFFE64CF0),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            PixelArtText(
                                text = date,
                                fontSize = 20.sp,
                                color = Color(0xFFE64CF0),
                                modifier = Modifier
                                    .padding(end = 16.dp, start = 8.dp),
                            )
                        }

                    }

                } else {
                    PixelArtText(
                        text = context.getString(R.string.no_active_subs),
                        fontSize = 28.sp
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(
                    text = context.getString(R.string.active_subs),
                    fontSize = 28.sp,
                    modifier = Modifier.testTag("activeSubsTitle")
                )
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtText(
                    text = subscriptions.size.toString(),
                    fontSize = 56.sp
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}