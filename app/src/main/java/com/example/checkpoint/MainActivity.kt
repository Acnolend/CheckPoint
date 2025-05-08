package com.example.checkpoint

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.checkpoint.ui.theme.CheckPointTheme
import com.example.checkpoint.ui.views.CreateSubscription
import com.example.checkpoint.ui.views.Home
import com.example.checkpoint.core.backend.api.appwrite.AuthService
import com.example.checkpoint.ui.components.RequestNotificationPermission
import com.example.checkpoint.ui.views.EditSubscription
import com.example.checkpoint.ui.views.EditUser
import com.example.checkpoint.ui.views.ImportSubscriptionsView
import com.example.checkpoint.ui.views.ListSubscription
import com.example.checkpoint.ui.views.Login
import com.example.checkpoint.ui.views.MenuView
import com.example.checkpoint.ui.views.Register
import com.example.checkpoint.ui.views.RetroLoadingScreen
import com.example.checkpoint.ui.views.Settings
import com.example.checkpoint.ui.views.Suggestions
import com.example.checkpoint.ui.views.Summary
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckPointTheme {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    RequestNotificationPermission { granted ->
                        if (!granted) {
                            Toast
                                .makeText(this, "Sin permiso de notificaciones", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

                val navController = rememberNavController()
                var isAuthenticated by remember { mutableStateOf<Boolean?>(null) }
                val authService = AuthService(this)
                var progress by remember { mutableFloatStateOf(0f) }

                LaunchedEffect(Unit) {
                    for (i in 1..10) {
                        delay(100)
                        progress = i * 0.1f
                        val isLoggedIn = authService.isUserLoggedIn()
                        isAuthenticated = isLoggedIn
                    }
                }

                if (isAuthenticated == null) {
                    RetroLoadingScreen(progress = progress)
                } else {
                    NavigationGraph(navController = navController, isAuthenticated = isAuthenticated!!)
                }

            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(navController: NavHostController, isAuthenticated: Boolean) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) "home" else "register",
    ) {
        composable("home") {
            Home(navController)
        }
        composable("create_subscription") {
            CreateSubscription(navController)
        }
        composable("edit_subscription") {
            EditSubscription(navController)
        }
        composable("register") {
            Register(navController)
        }
        composable("login") {
            Login(navController)
        }
        composable("menu_view") {
            MenuView(navController)
        }
        composable("list_subscription") {
            ListSubscription(navController)
        }
        composable("edit_user") {
            EditUser(navController)
        }
        composable("settings") {
            Settings(navController)
        }
        composable("import_subscriptions") {
            ImportSubscriptionsView(navController)
        }
        composable("summary") {
            Summary(navController)
        }
        composable("suggestions") {
            Suggestions(navController)
        }
    }
}