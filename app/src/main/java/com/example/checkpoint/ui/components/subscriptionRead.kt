package com.example.checkpoint.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.checkpoint.R
import com.example.checkpoint.application.services.cancelSubscriptionURL
import com.example.checkpoint.core.backend.domain.enumerate.SubscriptionCostType
import com.example.checkpoint.core.store.CurrencyStore
import kotlinx.coroutines.launch

@Composable
fun SubscriptionRead(
    name: String,
    imageUri: String,
    cost: String,
    costType: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isRenewalSoon: Boolean
    ) {

    val context: Context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val stringResId = when (costType) {
        SubscriptionCostType.DAILY.toString() -> R.string.daily
        SubscriptionCostType.WEEKLY.toString() -> R.string.weekly
        SubscriptionCostType.BIWEEKLY.toString() -> R.string.biweekly
        SubscriptionCostType.MONTHLY.toString() -> R.string.monthly
        SubscriptionCostType.BIMONTHLY.toString() -> R.string.bimonthly
        SubscriptionCostType.QUARTERLY.toString() -> R.string.quarterly
        SubscriptionCostType.SEMIANNUAL.toString() -> R.string.semiannual
        SubscriptionCostType.ANNUAL.toString() -> R.string.annual
        else -> R.string.app_name
    }
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(125.dp)
            .background(Color.White)
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Image",
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                PixelArtText(name, color = Color(0xFF4CC9F0))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PixelArtText(CurrencyStore.formatPrice(cost), color = Color(0xFF4CC9F0))
                    Spacer(modifier = Modifier.width(8.dp))
                    PixelArtText(context.getString(stringResId),color = Color(0xFF4CC9F0)
                    )
                }

                if (isRenewalSoon) {
                    PixelArtButton(
                        text = context.getString(R.string.cancel),
                        fontSize = 16.sp,
                        onClick = {
                            coroutineScope.launch {
                                val url = cancelSubscriptionURL(name)
                                if (!url.isNullOrBlank() && (url.startsWith("http://") || url.startsWith("https://"))) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(context, context.getString(R.string.no_url_cancel), Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, context.getString(R.string.no_url_cancel), Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        color = Color(0xFFFF4081)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_edit),
                    contentDescription = "Edit",
                    modifier = Modifier.size(24.dp).clickable { onEditClick() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.icon_delete),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp).clickable { onDeleteClick() }
                )
            }
        }
    }
}