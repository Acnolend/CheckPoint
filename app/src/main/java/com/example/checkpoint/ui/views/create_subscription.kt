package com.example.checkpoint.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.ui.components.ImageSubsription
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtText
import com.example.checkpoint.ui.components.PixelArtTextField


@Composable
fun CreateSubscription(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var isMonthly by remember { mutableStateOf(true) }
    var reminderDays by remember { mutableFloatStateOf(1f) }
    val normalizedReminderValue = remember(reminderDays, isMonthly) {
        if (isMonthly) {
            reminderDays.coerceIn(1f..30f)
        } else {
            (reminderDays / 30f).coerceIn(1f..12f)
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
                PixelArtText(
                    text = "CREAR SUSCRIPCIÓN",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE64CF0)
                )
                Spacer(modifier = Modifier.height(48.dp))
                ImageSubsription()
                Spacer(modifier = Modifier.height(40.dp))
                PixelArtTextField("NOMBRE", name, onTextChange = { name = it })
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier
                        .padding(start = 56.dp, end = 56.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PixelArtTextField("COSTE", cost, onTextChange = { cost = it })
                    Button(
                        onClick = {
                            if (!isMonthly && reminderDays > 0) {
                                reminderDays = 30f
                            }
                            isMonthly = !isMonthly
                        },
                        modifier = Modifier.align(Alignment.End)
                            .padding(top = 16.dp)
                    ) {
                        PixelArtText(if (isMonthly) "Mensual" else "Anual")
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                PixelArtText(if (isMonthly) "RECORDAR CADA ${normalizedReminderValue.toInt()} DÍAS" else "RECORDAR CADA ${normalizedReminderValue.toInt()} MESES")
                Slider(
                    value = normalizedReminderValue,
                    onValueChange = { newValue ->
                        reminderDays = if (isMonthly) {
                            newValue.coerceIn(1f..30f) // Mantén el valor entre 1 y 30 si es mensual
                        } else {
                            (newValue * 30f).coerceIn(1f..360f) // Convierte de meses a días si es anual
                        }
                    },
                    valueRange = if (isMonthly) 1f..30f else 1f..12f,
                    steps = if (isMonthly) 29 else 11,
                    modifier = Modifier.padding(start= 56.dp, end= 56.dp)
                )
                Button(
                    onClick = {
                        // Aquí guardas la suscripción en la base de datos o donde quieras
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PixelArtText("Guardar Suscripción")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    )
}