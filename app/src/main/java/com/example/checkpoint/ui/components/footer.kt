package com.example.checkpoint.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.checkpoint.R

@Composable
fun Footer(navController: NavController) {
    var showPopup by remember { mutableStateOf(false) }

    if (showPopup) {
        CreateOrImportPopup(navController = navController, onDismiss = { showPopup = false })
    }

    BottomAppBar(
        containerColor = Color(0xFF4CC9F0),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("home")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.icon_house),
                    contentDescription = "Left Image",
                    modifier = Modifier.size(32.dp).testTag("homeButton")
                )
            }
            IconButton(onClick = { showPopup = true
            }) {
                Image(
                    painter = painterResource(id = R.drawable.icon_add),
                    contentDescription = "Center Image",
                    modifier = Modifier.size(40.dp).testTag("createSubscriptionButton")
                )
            }
            IconButton(onClick = { navController.navigate("menu_view")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.icon_options),
                    contentDescription = "Right Image",
                    modifier = Modifier.size(32.dp).testTag("menuViewButton")
                )
            }
        }
    }
}