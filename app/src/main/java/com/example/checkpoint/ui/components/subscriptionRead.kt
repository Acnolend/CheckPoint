package com.example.checkpoint.ui.components

import android.net.Uri
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.checkpoint.R

@Composable
fun SubscriptionRead(
    name: String,
    imageUri: String,
    cost: String,
    costType: String,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
    ) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(100.dp)
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
                painter = rememberImagePainter(imageUri),
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
                    PixelArtText(cost + "€", color = Color(0xFF4CC9F0))
                    Spacer(modifier = Modifier.width(8.dp))
                    PixelArtText(costType, color = Color(0xFF4CC9F0))
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