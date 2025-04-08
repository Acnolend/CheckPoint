package com.example.checkpoint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun LinesRegisterLogin(isTop: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .background(color = if (isTop) Color(0xFF2D006C) else Color(0xFF4CC9F0))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .background(color = if (isTop) Color(0xFF480CA8) else Color(0xFF4895EF))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .background(color = if (isTop) Color(0xFF5A189A) else Color(0xFF4361EE))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp) // Aquí defines el grosor de la línea
            .background(Color(0xFF6A1FB7))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp) // Aquí defines el grosor de la línea
            .background(color = if (isTop) Color(0xFF4361EE) else Color(0xFF5A189A))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp) // Aquí defines el grosor de la línea
            .background(color = if (isTop) Color(0xFF4895EF) else Color(0xFF480CA8))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp) // Aquí defines el grosor de la línea
            .background(color = if (isTop) Color(0xFF4CC9F0) else Color(0xFF2D006C))
    )
}
