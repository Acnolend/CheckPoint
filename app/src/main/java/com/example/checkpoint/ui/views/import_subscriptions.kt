package com.example.checkpoint.ui.views

import com.example.checkpoint.core.backend.api.appwrite.GoogleService
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.checkpoint.ui.components.OwnScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImportSubscriptionsView(navController: NavController) {
    val subscriptions = remember { mutableStateListOf("Netflix", "Spotify", "Crunchyroll", "Disney+") }
    val selectedItems = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        val service = GoogleService()
        val fetchedSubscriptions = withContext(Dispatchers.IO) {
            service.getInboxEmails("x")
        }
        subscriptions.addAll(fetchedSubscriptions)
    }


    OwnScaffold(navController, content = { modifier ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Subscripciones detectadas en Gmail",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(subscriptions) { subscription ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Checkbox(
                            checked = selectedItems.contains(subscription),
                            onCheckedChange = { isChecked ->
                                if (isChecked) selectedItems.add(subscription)
                                else selectedItems.remove(subscription)
                            }
                        )
                        Text(text = subscription, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            Button(
                onClick = {
                    println("Subscripciones a importar: $selectedItems")
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Importar seleccionadas")
            }
        }
    })
}