package com.example.checkpoint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkpoint.core.store.CurrencyStore

@Composable
fun PaymentRead(
    name: String,
    amount: String,
    date: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF2D006C),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            PixelArtText(name, color = Color(0xFF4CC9F0), fontSize = 20.sp)

            Column(horizontalAlignment = Alignment.End) {
                PixelArtText("-${CurrencyStore.formatPrice(amount)}", color = Color(0xFFFF4081), fontSize = 20.sp)
                PixelArtText(date, color = Color(0xFF4CC9F0), fontSize = 20.sp)
            }
        }
    }
}
