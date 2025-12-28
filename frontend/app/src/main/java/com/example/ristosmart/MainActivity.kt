package com.example.ristosmart

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ristosmart.ui.screens.checkin.CheckinScreen
import com.example.ristosmart.ui.screens.kitchenstaff.KitchenStaffHomeScreen
import com.example.ristosmart.ui.screens.login.Forgot
import com.example.ristosmart.ui.screens.login.LogInScreen
import com.example.ristosmart.ui.screens.waiter.WaiterHomeScreen
import com.example.ristosmart.ui.theme.RistoSmartTheme

@Composable
fun RistoSmartApp(modifier: Modifier = Modifier) {
    //using a controller to handle navigation of the application
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier // Apply the modifier here
    ) {
        // 1. Login Route
        composable("login") {

            LogInScreen(
                // Callback used to navigate to the Forgot screen
                onNavigateToForgot = {
                    navController.navigate("forgot")
                },
                onLoginSuccess = { user ->
                    Log.d("Navigation", "User role: ${user.role}")
                    // Redirect to checkin screen regardless of role
                    navController.navigate("checkin") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // 2. Forgot Route
        composable("forgot") {
            Forgot(onNavigateBack = {
                navController.popBackStack()
            })
        }

        // 3. Checkin Route
        composable("checkin") {
            CheckinScreen(
                onNavigateToChef = {
                    navController.navigate("kitchen_staff_home") {
                        popUpTo("checkin") { inclusive = true }
                    }
                },
                onNavigateToWaiter = {
                    navController.navigate("waiter_home") {
                         popUpTo("checkin") { inclusive = true }
                    }
                }
            )
        }

        // 4. Waiter Home Route
        composable("waiter_home") {
            WaiterHomeScreen(
                onNavigateBack = {
                    // Navigate explicitly to checkin, popping the current stack
                    navController.navigate("checkin") {
                        popUpTo("waiter_staff_home") { inclusive = true }
                    }
                }
            )
        }

        // 5. Kitchen Staff Home Route
        composable("kitchen_staff_home") {
            KitchenStaffHomeScreen(
                onNavigateBack = {
                    // Navigate explicitly to checkin, popping the current stack
                    navController.navigate("checkin") {
                        popUpTo("kitchen_staff_home") { inclusive = true }
                    }
                }
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RistoSmartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RistoSmartApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
