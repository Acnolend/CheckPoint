package com.example.checkpoint.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtText
import kotlinx.coroutines.launch

@Composable
fun Settings(navController: NavController) {
    val context: Context = LocalContext.current
    val authService = AuthService(context)
    val coroutineScope = rememberCoroutineScope()

    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PixelArtText("AJUSTES")
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText("IDIOMA")
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText("MODO CLARO/OSCURO")
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtButton(
                    text = "CERRAR SESIÓN",
                    onClick = {
                        coroutineScope.launch {
                            authService.signOut()
                            navController.navigate("register")
                        }
                    },
                    fontSize = 24.sp
                )
            }
        }
    )
}