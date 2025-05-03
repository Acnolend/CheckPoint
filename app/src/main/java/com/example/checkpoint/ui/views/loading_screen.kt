package com.example.checkpoint.ui.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkpoint.R
import com.example.checkpoint.ui.components.PixelArtText

@Composable
fun RetroLoadingScreen(progress: Float) {
    val context: Context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PixelArtText(
                text = context.getString(R.string.loading),
                fontSize = 36.sp,
                color = Color(0xFF4CC9F0),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            RetroLoadingBar(progress)
        }
    }
}

@Composable
fun RetroLoadingBar(progress: Float) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(20.dp)
            .border(3.dp, Color.White, shape = RectangleShape)
            .background(Color.DarkGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width((280 * progress).dp)
                .background(Color(0xFFE64CF0))
        )
    }
}
