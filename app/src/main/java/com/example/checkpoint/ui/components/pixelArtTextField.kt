package com.example.checkpoint.ui.components
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkpoint.R

@Composable
fun PixelArtTextField(
    label: String,
    text: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
) {

    val isPasswordVisible = remember { androidx.compose.runtime.mutableStateOf(false) }
    val visualTransformation = if (isPassword && !isPasswordVisible.value) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }


    OutlinedTextField(
        value = text,
        label = { PixelArtText(label, color = Color(0xFF4CC9F0)) },
        onValueChange = onTextChange,
        textStyle = TextStyle(fontFamily = FontFamily(Font(R.font.pixel_art_font)), fontSize = 16.sp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                PixelArtText(text = errorMessage, color = Color.Red, fontSize = 12.sp)
            }
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { isPasswordVisible.value = !isPasswordVisible.value }) {
                        val icon = if (isPasswordVisible.value) {
                            painterResource(R.drawable.icon_options)
                        } else {
                            painterResource(R.drawable.icon_hiden_password)
                        }
                        Image(painter = icon, contentDescription = "Toggle password visibility", modifier = Modifier.size(24.dp))
                }
            }
        },
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor  = Color.White,
            errorContainerColor     = Color(0xFFFFD0D0),

            focusedTextColor        = Color.Black,
            unfocusedTextColor      = Color.Black,
            disabledTextColor       = Color.Black,
            errorTextColor          = Color.Red,

            cursorColor             = Color.Black,
            errorCursorColor        = Color.Red
        ),
        modifier = modifier
    )
}