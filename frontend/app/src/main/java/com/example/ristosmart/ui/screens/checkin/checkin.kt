package com.example.ristosmart.ui.screens.checkin

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckinScreen(
    viewModel: CheckinViewModel = viewModel(),
    onNavigateToChef: () -> Unit,
    onNavigateToWaiter: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var permissionError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    
    // State for button animation
    var isCheckingIn by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isCheckingIn) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "Button Scale Animation"
    )

    // Navigation side-effect
    LaunchedEffect(uiState.isLocationVerified) {
        if (uiState.isLocationVerified) {
            isCheckingIn = false // Stop animation
            when (uiState.userRole) {
                "chef" -> onNavigateToChef()
                "waiter" -> onNavigateToWaiter()
                // Add other roles if needed
            }
            viewModel.onLocationVerifyConsumed()
        }
    }

    // Function to get location
    fun getCurrentLocation() {
        isCheckingIn = true // Start loading/animation state
        locationError = null // Clear previous errors
        
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            
            // Suppressing permission check warning because we check permissions before calling this
            @SuppressLint("MissingPermission")
            val locationTask = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            )

            locationTask.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val isNearby = viewModel.verifyLocation(location)
                    if (isNearby) {
                        viewModel.onLocationVerified()
                        locationError = null
                    } else {
                        locationError = "You are not at the restaurant location!"
                        isCheckingIn = false
                    }
                } else {
                    locationError = "Unable to retrieve location. Make sure GPS is on."
                    isCheckingIn = false
                }
            }.addOnFailureListener {
                locationError = "Error getting location: ${it.message}"
                isCheckingIn = false
            }
        } catch (e: Exception) {
            locationError = "Error: ${e.message}"
            isCheckingIn = false
        }
    }

    // Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            
            if (fineLocationGranted || coarseLocationGranted) {
                permissionError = null
                getCurrentLocation()
            } else {
                permissionError = "Location permission is required to check in."
                isCheckingIn = false
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RistoSmart") },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.White,
                    containerColor = Color.Blue
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {

            Text(text = "Welcome ${uiState.userRole.capitalize()}")

            Text(
                text = "role: ${uiState.userRole}",
                style = MaterialTheme.typography.bodyLarge
            )

            if (permissionError != null) {
                Text(
                    text = permissionError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            if (locationError != null) {
                Text(
                    text = locationError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Card(
                border = BorderStroke(1.dp, Color.Blue),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Current status")
                    Text(text = uiState.status) // Received from API

                    Button(
                        onClick = { 
                            if (!isCheckingIn) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier.graphicsLayer(
                            scaleX = buttonScale,
                            scaleY = buttonScale
                        ),
                        enabled = !isCheckingIn
                    ) {
                        if (isCheckingIn) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Check In", color = Color.Black)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "At home from:")
                        Text(text = uiState.time) // Received from API
                    }
                }
            }
        }
    }
}

// Extension function helper
fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
