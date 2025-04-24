package com.example.checkpoint.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.ui.components.LinesRegisterLogin
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.PixelArtTextField
import com.example.checkpoint.ui.views.data_model.validateUserEmailInput
import com.example.checkpoint.ui.views.data_model.validateUserNameInput
import com.example.checkpoint.ui.views.data_model.validateUserPasswordInput
import androidx.compose.ui.platform.testTag
import com.example.checkpoint.R
import kotlinx.coroutines.launch

@SuppressLint("DiscouragedApi")
@Composable
fun Register(navController: NavController) {
    val context: Context = LocalContext.current
    val authService = AuthService(context)


    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userNameError by remember { mutableStateOf<String?>(null) }
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
                .width(350.dp)
                .height(390.dp)
                .background(Color(0xFF4895EF))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                PixelArtTextField(
                    context.getString(R.string.user_name),
                    name,
                    onTextChange = {
                        name = it
                        userNameError = validateUserNameInput(it)
                    },
                    isError = userNameError != null,
                    errorMessage = userNameError?.let { context.getString(context.resources.getIdentifier(it, "string", context.packageName)) },
                    modifier = Modifier.testTag("usernameField")
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
                    isPassword = true,
                    modifier = Modifier.testTag("passwordField")
                )
                Spacer(modifier = Modifier.height(4.dp))
                PixelArtTextField(
                    context.getString(R.string.user_email),
                    email,
                    onTextChange = {
                        email = it
                        userEmailError = validateUserEmailInput(it)
                    },
                    isError = userEmailError != null,
                    errorMessage = userEmailError?.let { context.getString(context.resources.getIdentifier(it, "string", context.packageName)) },
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.testTag("emailField")
                )

                PixelArtText(context.getString(R.string.login), modifier = Modifier.testTag("toLoginButton").clickable { navController.navigate("login") })
                Spacer(modifier = Modifier.height(12.dp))
                PixelArtButton(
                    text = context.getString(R.string.register),
                    onClick = {
                        coroutineScope.launch {
                            val result = authService.signUp(email, password, name)
                            if (result.isSuccess) {
                                navController.navigate("home")
                            } else {
                                val error = result.exceptionOrNull()?.message
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    fontSize = 24.sp,
                    errorMessages = listOf(userEmailError.orEmpty(), userPasswordError.orEmpty(), userNameError.orEmpty()),
                    requiredFields = listOf(email, password, name),
                    modifier = Modifier.testTag("registerButton")
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        LinesRegisterLogin(false)
    }
}