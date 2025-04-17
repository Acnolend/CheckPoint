package com.example.checkpoint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun PixelArtButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    fontSize: TextUnit = 16.sp,
    color: Color = Color(0xFFE64CF0),
    errorMessages: List<String>? = null,
    requiredFields: List<String>? = null,
) {
    val areFieldsFilled = requiredFields?.all { it.isNotEmpty() } ?: true
    val noErrors = errorMessages?.all { it.isEmpty() } ?: true

    val isButtonEnabled = areFieldsFilled && noErrors

    val buttonColor = if (isButtonEnabled) color else Color.Gray
    val textColor = if (isButtonEnabled) Color.White else Color.DarkGray


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(buttonColor)
            .clickable(enabled = isButtonEnabled) {
                if (isButtonEnabled) onClick()
            }
            .padding(vertical = 12.dp, horizontal = 24.dp)
    ) {
        PixelArtText(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize,
            color = textColor
        )
    }
}