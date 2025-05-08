package com.example.checkpoint.ui.views

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.application.services.generateSimilarSubscriptions
import com.example.checkpoint.core.backend.domain.entities.Subscription
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.SubscriptionSelector
import kotlinx.coroutines.launch


@Composable
fun Suggestions(navController: NavController) {
    val context: Context = LocalContext.current
    var selectedSubscription by remember { mutableStateOf<Subscription?>(null) }
    var suggestionText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PixelArtText(context.getString(R.string.suggestions), color = Color(0xFFE64CF0), fontSize = 48.sp)
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtText(context.getString(R.string.suggestion_explanation))
                Spacer(modifier = Modifier.height(32.dp))

                SubscriptionSelector(
                    modifier = Modifier.padding(bottom = 24.dp),
                    onSubscriptionSelected = { subscription ->
                        selectedSubscription = subscription
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PixelArtButton(context.getString(R.string.generate), onClick = {
                    selectedSubscription?.let { subscription ->
                        isLoading = true
                        suggestionText = ""
                        coroutineScope.launch {
                            val result = generateSimilarSubscriptions(
                                subscriptionName = subscription.name.name,
                                subscriptionPrice = subscription.cost.cost.toString()
                            )
                            suggestionText = result ?: context.getString(R.string.error_generating_suggestions)
                            isLoading = false
                        }
                    }
                }, fontSize = 24.sp)

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                } else if (suggestionText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2D006C), shape = RoundedCornerShape(16.dp))
                            .padding(16.dp),
                    ) {
                        PixelArtText(
                            text = suggestionText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    )
}
