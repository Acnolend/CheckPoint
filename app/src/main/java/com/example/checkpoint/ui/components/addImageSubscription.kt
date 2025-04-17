package com.example.checkpoint.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import coil.compose.rememberAsyncImagePainter

@Composable
fun ImageSubscription(
    onImageSelected: (Uri) -> Unit,
    existingImageUrl: String? = null,
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            onImageSelected(it)
        }
    }

    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { launcher.launch("image/*") }
            .testTag("imageSubscription"),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUri != null -> {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            existingImageUrl != null -> {
                Image(
                    painter = rememberAsyncImagePainter(existingImageUrl),
                    contentDescription = "Imagen actual de la suscripción",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Seleccionar imagen",
                    tint = Color.Black,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}
