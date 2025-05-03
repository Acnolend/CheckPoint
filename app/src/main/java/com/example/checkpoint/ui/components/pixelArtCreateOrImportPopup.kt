package com.example.checkpoint.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import kotlinx.coroutines.launch

@Composable
fun CreateOrImportPopup(navController: NavController, onDismiss: () -> Unit) {
    val context: Context = LocalContext.current
    val authService = AuthService(context)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            PixelArtText(context.getString(R.string.select_option))
        },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("create_subscription")
                            onDismiss()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.icon_create), contentDescription = "Crear", modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    PixelArtText(context.getString(R.string.create_subscription_popup))
                }
                HorizontalDivider()
                val coroutineScope = rememberCoroutineScope()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            coroutineScope.launch {
                                val isLoggedInWithGoogle = authService.isUserLoggedInWithGoogle()
                                if (isLoggedInWithGoogle) {
                                    navController.navigate("import_subscriptions")
                                    onDismiss()
                                } else {
                                    Toast.makeText(context, "Necesitas estar logeado con Google para usar esta función", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painter = painterResource(id = R.drawable.icon_import), contentDescription = "Importar", modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    PixelArtText(context.getString(R.string.import_subscription))
                }
            }
        },
        confirmButton = {
            Spacer(modifier = Modifier.height(0.dp))
        },
        dismissButton = {
            Spacer(modifier = Modifier.height(0.dp))
        },
        containerColor = Color(0xFF2D006C),
    )
}
