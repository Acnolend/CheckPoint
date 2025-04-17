package com.example.checkpoint.ui.views

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtTextField
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.views.data_model.validateUserEmailInput
import com.example.checkpoint.ui.views.data_model.validateUserNameInput
import com.example.checkpoint.ui.views.data_model.validateUserPasswordInput
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditUser(navController: NavController) {
    val context: Context = LocalContext.current
    val authService = AuthService(context)
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var currentEmail by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var userNameError by remember { mutableStateOf<String?>(null) }
    var userCurrentPasswordError by remember { mutableStateOf<String?>(null) }
    var userNewPasswordError by remember { mutableStateOf<String?>(null) }
    var userEmailError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val resultName = authService.getUserName()
        val resultEmail = authService.getUserEmail()
        resultName.onSuccess {
            userName ->
            name = userName
        }.onFailure { error ->
            println("Error: ${error.message}")
        }
        resultEmail.onSuccess {
                userEmail ->
            currentEmail = userEmail
        }.onFailure { error ->
            println("Error: ${error.message}")
        }
    }

    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.icon_user),
                    contentDescription = "Left Image",
                    modifier = Modifier.fillMaxSize().clip(RectangleShape)
                )
                Spacer(modifier = Modifier.height(32.dp))

                PixelArtTextField(
                    "NOMBRE",
                    name,
                    onTextChange = {
                        name = it
                        userNameError = validateUserNameInput(it)
                                   },
                    isError = userNameError != null,
                    errorMessage = userNameError
                )
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtText("PARA CAMBIAR CORREO Y CONTRASEÑA DEBES PONER LA CONTRASEÑA ACTUAL", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtTextField(
                    "CONTRASEÑA ACTUAL",
                    currentPassword,
                    onTextChange =
                    { currentPassword= it
                        userCurrentPasswordError = validateUserPasswordInput(it)
                    },
                    isError = userCurrentPasswordError != null,
                    errorMessage = userCurrentPasswordError,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtTextField(
                    "CONTRASEÑA NUEVA",
                    newPassword,
                    onTextChange =
                    { newPassword= it
                        userNewPasswordError = validateUserPasswordInput(it)
                    },
                    isError = userNewPasswordError != null,
                    errorMessage = userNewPasswordError,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                PixelArtTextField(
                    "CORREO",
                    currentEmail,
                    onTextChange = {
                        currentEmail = it
                        userEmailError = validateUserEmailInput(it)
                    },
                    isError = userEmailError != null,
                    errorMessage = userEmailError,
                    keyboardType = KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(32.dp))
                PixelArtButton(
                    text = "GUARDAR",
                    onClick = {
                        coroutineScope.launch {
                            authService.editUser(name, currentEmail, newPassword, currentPassword)
                            navController.navigate("home")
                        }
                    },
                    fontSize = 24.sp,
                )
                Spacer(modifier = Modifier.height(32.dp))
                PixelArtButton(
                    text = "BORRAR DATOS",
                    onClick = {
                        coroutineScope.launch {
                            authService.deleteUserAndSubscriptions(authService.getUserIdActual().toString().substringAfter("(").substringBefore(")"))
                        }
                    },
                    fontSize = 24.sp,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}