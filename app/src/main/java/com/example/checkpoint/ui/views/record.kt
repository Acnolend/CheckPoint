package com.example.checkpoint.ui.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.checkpoint.R
import com.example.checkpoint.core.backend.api.appwrite.AppwriteService
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.core.backend.api.appwrite.PaymentRepository
import com.example.checkpoint.ui.components.OwnScaffold
import com.example.checkpoint.ui.components.PaymentRead
import com.example.checkpoint.ui.components.PixelArtButton
import com.example.checkpoint.ui.components.PixelArtPopup
import com.example.checkpoint.ui.components.PixelArtText
import kotlinx.coroutines.launch
import org.json.JSONObject


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Record(navController: NavController) {
    val context = LocalContext.current
    val payments = remember { mutableStateListOf<JSONObject>() }
    val appwriteService = AppwriteService(context)
    val authService = AuthService(context)
    val paymentRepository = PaymentRepository(appwriteService)
    val showPopup = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pageSize = 10
    val currentPage = remember { mutableIntStateOf(1) }
    val displayedPayments = payments.take(currentPage.intValue * pageSize)

    LaunchedEffect(Unit) {
        val userId = authService.getUserIdActual().toString().substringAfter("(").substringBefore(")")
        payments.clear()
        payments.addAll(paymentRepository.getAllPayments(userId))
    }

    if (showPopup.value) {
        PixelArtPopup(
            onDismissRequest = { showPopup.value = false },
            title = context.getString(R.string.confirm_delete_title),
            message = context.getString(R.string.confirm_delete_message),
            onConfirm = {
                coroutineScope.launch {
                    val userId = authService.getUserIdActual().toString().substringAfter("(").substringBefore(")")
                    paymentRepository.deleteAllPayments(userId)
                    payments.clear()
                    showPopup.value = false
                }
            }
        )
    }

    OwnScaffold(navController, isScrollable = false,
        content = { modifier ->
            Column(
                modifier = modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                PixelArtText(
                    context.getString(R.string.payment_history),
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFE64CF0),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    PixelArtButton(
                        text = context.getString(R.string.clean),
                        onClick = {
                            showPopup.value = true
                        },
                        fontSize =16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedPayments) { payment ->
                        val name = payment.getString("subscriptionName")
                        val amount = payment.getString("amount")
                        val date = payment.getString("date")

                        PaymentRead(
                            name = name,
                            amount = amount,
                            date = date
                        )
                    }

                    if (displayedPayments.size < payments.size) {
                        item {
                            LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
                                val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                                if (lastVisibleItem != null && lastVisibleItem.index == displayedPayments.size - 1) {
                                    currentPage.intValue += 1
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}