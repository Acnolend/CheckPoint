package com.example.checkpoint.ui.views

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.application.services.getNextRenewalDate
import com.example.checkpoint.application.services.serviceReadSubscription
import com.example.checkpoint.application.usecases.usecaseReadSubscription
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.core.backend.api.appwrite.SubscriptionRepository
import com.example.checkpoint.core.store.SubscriptionStore
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtText

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Home(navController: NavController) {
    val context: Context = LocalContext.current
    val appwriteService = AppwriteService(context)
    val authService = AuthService(context)
    val subscriptionRepository = SubscriptionRepository(appwriteService)
    val readSubscriptionUseCase: usecaseReadSubscription = serviceReadSubscription(subscriptionRepository)

    val subscriptions by SubscriptionStore.subscriptions.collectAsState()
    var nextRenewalDate by remember { mutableStateOf<Pair<String, String>?>(null) }


    LaunchedEffect(Unit) {
        val userId = authService.getUserIdActual().toString().substringAfter("(").substringBefore(")")
        val data = readSubscriptionUseCase.fetchByAll(userId)
        SubscriptionStore.setSubscriptions(data)
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
                    text = "TOTAL GASTADO ESTE MES",
                    fontSize = 28.sp
                )
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(
                    text = "64€",
                    fontSize = 56.sp
                )
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(
                    text = "RENOVACIÓN MAS CERCANA",
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
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            PixelArtText(
                                text = name,
                                fontSize = 20.sp,
                                color = Color(0xFFE64CF0),
                                modifier = Modifier
                                    .padding(end = 16.dp, start = 8.dp),
                            )
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
                        text = "¡NO HAY SUBSCRIPCIONES!",
                        fontSize = 28.sp
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(
                    text = "SUSCRIPCIONES ACTIVAS",
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