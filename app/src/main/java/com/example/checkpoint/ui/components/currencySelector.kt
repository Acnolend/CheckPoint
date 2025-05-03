package com.example.checkpoint.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.checkpoint.core.store.Currency

@Composable
fun CurrencySelector(
    modifier: Modifier = Modifier,
    currentCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val currencyMap = mapOf(
        Currency.USD to "USD",
        Currency.EUR to "EUR",
        Currency.GBP to "GBP"
    )

    val selectedText = currencyMap[currentCurrency] ?: "Seleccionar divisa"

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .border(1.dp, Color(0xFF4CC9F0))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PixelArtText(text = selectedText, color = Color(0xFF4CC9F0), fontSize = 24.sp)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir selector de divisa",
                    tint = Color(0xFF4CC9F0),
                    modifier = Modifier
                        .size(32.dp)
                        .padding(start = 8.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            currencyMap.forEach { (currency, label) ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onCurrencySelected(currency)
                }, text = { PixelArtText(label) })
            }
        }
    }
}
