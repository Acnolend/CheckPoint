package com.example.checkpoint.ui.views

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.core.store.Currency
import com.example.checkpoint.core.store.CurrencyStore
import com.example.checkpoint.core.store.LanguageStore
import com.example.checkpoint.ui.components.CurrencySelector
import com.example.checkpoint.ui.components.LanguageSelector
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtText
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun Settings(navController: NavController) {
    val context: Context = LocalContext.current
    val authService = AuthService(context)
    val coroutineScope = rememberCoroutineScope()
    val prefs = context.getSharedPreferences("language_preferences", Context.MODE_PRIVATE)
    var currentLang by remember { mutableStateOf(prefs.getString("language", Locale.getDefault().language) ?: "es") }
    val selectedCurrency by CurrencyStore.selectedCurrency.collectAsState()

    OwnScaffold(navController,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(context.getString(R.string.settings), fontSize = 48.sp)
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(context.getString(R.string.lenguage), fontSize = 32.sp)
                Spacer(modifier = Modifier.height(32.dp))
                LanguageSelector(currentLanguage = currentLang) { selectedLanguage ->
                    LanguageStore.setLanguage(context, selectedLanguage)
                    currentLang = selectedLanguage
                }
                Spacer(modifier = Modifier.height(32.dp))
                PixelArtText(context.getString(R.string.currency), fontSize = 32.sp)
                Spacer(modifier = Modifier.height(32.dp))
                CurrencySelector(currentCurrency = selectedCurrency) { selectedCurrency ->
                    CurrencyStore.setCurrency(selectedCurrency)
                }
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtText(context.getString(R.string.light_dark), fontSize = 32.sp)
                Spacer(modifier = Modifier.height(48.dp))
                PixelArtButton(
                    text = context.getString(R.string.logout),
                    onClick = {
                        coroutineScope.launch {
                            authService.signOut()
                            navController.navigate("register")
                        }
                    },
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    )
}