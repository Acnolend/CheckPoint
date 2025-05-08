package com.example.checkpoint.ui.views

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.core.store.SubscriptionStore
import com.example.checkpoint.ui.components.MonthlyBarChart
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.SummarySubscriptionList
import com.example.checkpoint.ui.components.YearlyBarChart

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Summary(navController: NavController) {
    val context: Context = LocalContext.current
    val subscriptions by SubscriptionStore.subscriptions.collectAsState()
    var isMonthlyMode by remember { mutableStateOf(true) }

    OwnScaffold(
        navController = navController,
        content = { modifier ->
            Column(modifier = modifier.padding(16.dp)) {
                if (isMonthlyMode) {
                    MonthlyBarChart(subscriptions)
                } else {
                    YearlyBarChart(subscriptions)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    PixelArtButton(
                        text = if (isMonthlyMode) context.getString(R.string.view_annual) else context.getString(R.string.view_monthly),
                        onClick = { isMonthlyMode = !isMonthlyMode }
                    )
                }

                SummarySubscriptionList(subscriptions, isMonthlyMode)
            }
        }
    )
}