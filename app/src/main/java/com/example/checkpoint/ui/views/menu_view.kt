package com.example.checkpoint.ui.views

import com.example.checkpoint.ui.components.PixelArtButton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.ui.components.OwnScaffold

@Composable
fun MenuView(navController: NavController) {
    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                PixelArtButton("SUMARIO", fontSize = 40.sp, onClick = { navController.navigate("home") })
                Spacer(modifier = Modifier.height(96.dp))
                PixelArtButton("HISTORIAL", fontSize = 40.sp,  onClick = { navController.navigate("home") })
                Spacer(modifier = Modifier.height(96.dp))
                PixelArtButton("LISTAR", fontSize = 40.sp,  onClick = { navController.navigate("list_subscription") })
            }
        }
    )
}