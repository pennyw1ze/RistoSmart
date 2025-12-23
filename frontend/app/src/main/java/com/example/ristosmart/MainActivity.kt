package com.example.ristosmart

import android.os.Bundle
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
import com.example.ristosmart.ui.screens.LogInScreen
import com.example.ristosmart.ui.screens.Forgot
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
                }
            )
        }

        // 2. Forgot Route
        composable("forgot") {
            // Here forgotScreen()
            Forgot()
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RistoSmartTheme {Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                RistoSmartApp(modifier = Modifier.padding(innerPadding))
            }
            }
        }
    }
}
