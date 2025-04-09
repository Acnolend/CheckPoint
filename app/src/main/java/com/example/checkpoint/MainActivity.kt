package com.example.checkpoint

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.checkpoint.ui.views.EditSubscription
import com.example.checkpoint.ui.views.EditUser
import com.example.checkpoint.ui.views.ListSubscription
import com.example.checkpoint.ui.views.Login
import com.example.checkpoint.ui.views.MenuView
import com.example.checkpoint.ui.views.Register

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckPointTheme {
                val navController = rememberNavController()
                var isAuthenticated by remember { mutableStateOf(false) }
                val authService = AuthService(this)

                LaunchedEffect(Unit) {
                    val isLoggedIn = authService.isUserLoggedIn()
                    isAuthenticated = isLoggedIn
                }

                NavigationGraph(navController = navController, isAuthenticated = isAuthenticated)
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
    }
}