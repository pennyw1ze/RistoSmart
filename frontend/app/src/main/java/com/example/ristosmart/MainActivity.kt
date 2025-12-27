package com.example.ristosmart


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                    when (user.role) {
                        "waiter" -> {
                            navController.navigate("waiter_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        "chef" -> {
                            navController.navigate("kitchen_staff_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        "manager" -> {
                            // TODO: Implement Manager Screen
                            // For now, maybe navigate to a generic placeholder or keep on login
                             Log.d("Navigation", "Manager screen not implemented yet")
                        }
                        "cashier" -> {
                            // TODO: Implement Cashier Screen
                            Log.d("Navigation", "Cashier screen not implemented yet")
                        }
                        else -> {
                            Log.e("Navigation", "Unknown role: ${user.role}")
                        }
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

        // 3. Waiter Home Route
        composable("waiter_home") {
            WaiterHomeScreen()
        }

        // 4. Kitchen Staff Home Route
        composable("kitchen_staff_home") {
            KitchenStaffHomeScreen()
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
                RistoSmartApp(modifier = Modifier)

            }
        }
}

