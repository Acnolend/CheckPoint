package com.example.checkpoint.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.ui.components.LinesRegisterLogin
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.PixelArtTextField
import com.example.checkpoint.ui.views.data_model.validateUserEmailInput
import com.example.checkpoint.ui.views.data_model.validateUserPasswordInput
import kotlinx.coroutines.launch

@SuppressLint("DiscouragedApi")
@Composable
fun Login(navController: NavController) {
    val context: Context = LocalContext.current
    val authService = AuthService(context)

    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userPasswordError by remember { mutableStateOf<String?>(null) }
    var userEmailError by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LinesRegisterLogin(true)
        Spacer(modifier = Modifier.height(32.dp))
        PixelArtText("CHECKPOINT", color = Color(0xFF4CC9F0), fontSize = 56.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        PixelArtText("DONDE GUARDAR TUS RECORDATORIOS", color = Color(0xFFE64CF0), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(350.dp)
                .background(Color(0xFF4895EF))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                PixelArtTextField(
                    context.getString(R.string.user_email),
                    email,
                    onTextChange = {
                        email = it
                        userEmailError = validateUserEmailInput(it)
                    },
                    isError = userEmailError != null,
                    errorMessage = userEmailError?.let { context.getString(context.resources.getIdentifier(it, "string", context.packageName)) },
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(4.dp))
                PixelArtTextField(
                    context.getString(R.string.user_password),
                    password,
                    onTextChange =
                    { password = it
                        userPasswordError = validateUserPasswordInput(it)
                    },
                    isError = userPasswordError != null,
                    errorMessage = userPasswordError?.let { context.getString(context.resources.getIdentifier(it, "string", context.packageName)) },
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                PixelArtText(context.getString(R.string.register), modifier = Modifier.clickable { navController.navigate("register") })
                Spacer(modifier = Modifier.height(16.dp))
                PixelArtButton(
                    text = context.getString(R.string.login),
                    onClick = {
                        coroutineScope.launch {
                            val result = authService.signIn(email, password)
                            if (result.isSuccess) {
                                navController.navigate("home")
                            } else {
                                val error = result.exceptionOrNull()?.message
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    fontSize = 24.sp,
                    errorMessages = listOf(userEmailError.orEmpty(), userPasswordError.orEmpty()),
                    requiredFields = listOf(email, password)
                )
                Spacer(modifier = Modifier.height(16.dp))
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                (context as? ComponentActivity)?.let {
                                    authService.signInWithGoogle(
                                        it
                                    )
                                }
                                navController.navigate("home")
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                              },
                    modifier = Modifier
                        .clip(RectangleShape)
                        .size(40.dp)

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_google),
                        contentDescription = "Center Image",
                        modifier = Modifier.fillMaxSize().clip(RectangleShape)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        LinesRegisterLogin(false)
    }
}