package com.example.ristosmart

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ristosmart.repository.TokenRepository
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ristosmart.ui.screens.checkin.CheckinScreen
import com.example.ristosmart.ui.screens.inventory.InventoryScreen
import com.example.ristosmart.ui.screens.kitchenstaff.KitchenStaffHomeScreen
import com.example.ristosmart.ui.screens.login.Forgot
import com.example.ristosmart.ui.screens.login.LogInScreen
import com.example.ristosmart.ui.screens.orders.OrdersScreen
import com.example.ristosmart.ui.screens.tables.TableScreen
import com.example.ristosmart.ui.screens.waiter.WaiterHomeScreen
import com.example.ristosmart.ui.screens.camera.CameraScreen
import com.example.ristosmart.ui.theme.RistoSmartTheme
import com.example.ristosmart.ui.theme.ThemeManager

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
                },
                onNavigateToOrders = {
                    navController.navigate("orders") {
                    }
                },
                onNavigateToHome = {
                    navController.navigate("kitchen_staff_home") {
                    }
                },
                onNavigateToInventory = {
                    navController.navigate("inventory") {
                    }
                }
            )
        }

        composable("orders") {
            OrdersScreen(
                onNavigateToHome = {
                    navController.navigate("kitchen_staff_home") {
                    }
                },
                onNavigateToTables = { tableId ->
                    navController.navigate("tables/$tableId") {
                    }
                },
                onNavigateToInventory = {
                    navController.navigate("inventory") {
                    }
                }
            )
        }

        composable("tables/{tableId}",
            arguments = listOf(navArgument("tableId") { type = NavType.IntType }))
        {
            backStackEntry ->
            // Extract the argument safely
            val tableId = backStackEntry.arguments?.getInt("tableId") ?: 0
            TableScreen(
                onNavigateToOrders = {
                    navController.navigate("orders") {
                    }
                },
                onNavigateToHome = {
                    navController.navigate("kitchen_staff_home") {
                    }
                },
                onNavigateToInventory = {
                    navController.navigate("inventory") {
                    }
                }
            )
        }

        composable("inventory") {
            //I don't have inventory yet
            InventoryScreen(
                onNavigateToHome = {
                    navController.navigate("kitchen_staff_home") {
                    }
                },
                onNavigateToOrders = {
                    navController.navigate("orders") {
                    }
                },
                onNavigateToInventory = {
                    navController.navigate("inventory") {
                    }
                },
                onNavigateToCamera = {
                    navController.navigate("camera") {
                    }
                }
            )
        }

        composable("camera"){
            CameraScreen(
                onNavigateToHome = {
                    navController.navigate("kitchen_staff_home") {
                    }
                },
                onNavigateToOrders = {
                    navController.navigate("orders") {
                    }
                },
                onNavigateToInventory = {
                    navController.navigate("inventory") {
                    }
                }
            )
        }
    }
}

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenRepository.init(applicationContext)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            val isDarkTheme = ThemeManager.isDarkTheme.value
            RistoSmartTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RistoSmartApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lightSensor?.also { light ->
            sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightValue = event.values[0]
            // Set a threshold for low light, e.g., < 50 lux
            ThemeManager.setDarkTheme(lightValue < 50f)
        }
    }
}
