package com.example.checkpoint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun OwnScaffold(
    navController: NavController,
    content: @Composable (Modifier) -> Unit,
    color: Color = Color(0xFF4895EF),
    isScrollable: Boolean = true
) {
    Scaffold(
        topBar = {
            Header(navController)
        },
        bottomBar = {
            Footer(navController)
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(paddingValues)
                    .let {
                        if (isScrollable) it.verticalScroll(rememberScrollState()) else it
                    }
            ) {
                content(Modifier)
            }
        }
    )
}
