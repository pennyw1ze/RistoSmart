package com.example.ristosmart.ui.screens.camera

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ristosmart.ui.scanner.BarcodeScannerScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    viewModel: CameraViewModel = viewModel(),
    onNavigateToHome: ()-> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToInventory: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    val items = listOf("Tables", "Home", "Inventory")
    val icons = listOf(Icons.Filled.TableRestaurant, Icons.Filled.Home, Icons.Filled.Inventory2)

    // 1. Setup the permission state
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // 2. Launch request when the screen opens if not granted
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory") }, //should I change this?
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = Color.White,
                    containerColor = Color.Blue
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant, // Background color
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = uiState.selectedNavIndex == index,
                        onClick = { viewModel.onNavBarBtnPressed(index)
                            when (index) {
                                0 -> onNavigateToOrders()
                                1 -> onNavigateToHome()
                                2 -> onNavigateToInventory()
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) {
        innerPadding ->
        //place a btn in the centre of the screen with written "scan the qr code of the product"


        if(uiState.isScanning){
            BarcodeScannerScreen(
                    onBarcodeScanned = { barcode ->
                        viewModel.onBarcodeFound(barcode)
                    }
                )
        }
        if(uiState.showScanBtn && !uiState.isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Button(
                    onClick = { viewModel.onScanClicked() }
                ) {
                    // Optional: Add an icon for better UX
                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Scan the QR code of the product")
                }
            }
        }
        if(uiState.showResults) {
            // Local state inside the screen (or move to VM if preferred)
            // Note: Since this block is recomposed, we need 'remember' to persist state
            // But we need to reset it if 'showResults' becomes false.
            // A simple way is to use a key in remember.
            var isAnimFinished by androidx.compose.runtime.remember(uiState.showResults) {
                androidx.compose.runtime.mutableStateOf(false)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                if (!isAnimFinished) {
                    // 1. Show the Google Pay style animation
                    SuccessAnimation(
                        modifier = Modifier.width(120.dp).height(
                            120.dp
                        ),
                        onAnimationFinished = { isAnimFinished = true }
                    )
                } else {
                    // 2. Show the actual Result Card
                    androidx.compose.material3.Card(
                        modifier = Modifier.padding(32.dp),
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        )
                    ) {
                        // ... your existing Column content ...
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
                        ) {
                            // ... rest of your UI ...
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Scanned",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.width(48.dp)
                            )
                            Text(
                                text = "Barcode Detected",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = uiState.scannedCode,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            // ... Buttons ...
                            androidx.compose.foundation.layout.Row(
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                            ) {
                                androidx.compose.material3.OutlinedButton(onClick = { viewModel.onRetryClicked() }) {
                                    Text("Retry")
                                }
                                androidx.compose.material3.Button(onClick = { }) {
                                    Text("Add Item")
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun SuccessAnimation(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit
) {
    // State for the circle scale (0f -> 1f)
    val scale = remember { androidx.compose.animation.core.Animatable(0f) }
    // State for the checkmark path progress (0f -> 1f)
    val checkProgress = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. Expand the circle pop
        scale.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 400,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            )
        )
        // 2. Draw the checkmark
        checkProgress.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(
                durationMillis = 300,
                easing = androidx.compose.animation.core.LinearOutSlowInEasing
            )
        )
        // 3. Wait a beat, then trigger finished callback
        kotlinx.coroutines.delay(500)
        onAnimationFinished()
    }

    Box(
        modifier = modifier,
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        // The Blue Circle
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val center = this.center
            val maxRadius = size.minDimension / 2

            // Draw Circle
            drawCircle(
                color = androidx.compose.ui.graphics.Color(0xFF4285F4), // Google Blue-ish
                radius = maxRadius * scale.value
            )

            // Draw Checkmark
            if (scale.value > 0.8f) { // Start drawing tick when circle is mostly open
                val path = androidx.compose.ui.graphics.Path().apply {
                    // Coordinates relative to center/radius
                    // Start of tick (left)
                    moveTo(center.x - maxRadius * 0.4f, center.y)
                    // Bottom point
                    lineTo(center.x - maxRadius * 0.1f, center.y + maxRadius * 0.3f)
                    // End point (top right)
                    lineTo(center.x + maxRadius * 0.5f, center.y - maxRadius * 0.4f)
                }

                // Use PathMeasure to trim the path based on progress
                val pathMeasure = androidx.compose.ui.graphics.PathMeasure()
                pathMeasure.setPath(path, false)

                val partialPath = androidx.compose.ui.graphics.Path()
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathMeasure.length * checkProgress.value,
                    destination = partialPath,
                    startWithMoveTo = true
                )

                drawPath(
                    path = partialPath,
                    color = androidx.compose.ui.graphics.Color.White,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 12.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }
        }
    }
}
