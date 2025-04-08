package com.example.checkpoint.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.checkpoint.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.navigate("") },
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(40.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_user),
                        contentDescription = "Left Image",
                        modifier = Modifier.fillMaxSize().clip(RectangleShape)

                    )
                }
                Spacer(modifier = Modifier.width(128.dp))
                IconButton(
                    onClick = { navController.navigate("") },
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(40.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon),
                        contentDescription = "Center Image",
                        modifier = Modifier.fillMaxSize().clip(RectangleShape)
                    )
                }
                Spacer(modifier = Modifier.width(128.dp))
                IconButton(
                    onClick = { navController.navigate("") },
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(40.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_settings),
                        contentDescription = "Right Image",
                        modifier = Modifier.fillMaxSize().clip(RectangleShape)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF2D006C)
        ),
    )
}