package com.example.ristosmart.ui.screens.kitchenstaff

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ristosmart.ui.screens.waiter.WaiterTablesViewModel
import com.example.ristosmart.ui.screens.waiter.WaiterTablesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenStaffHomeScreen(
    viewModel: KitchenStaffViewModel = viewModel(),
    tablesViewModel: WaiterTablesViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // State for button animation
    var isCheckingOut by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isCheckingOut) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "Button Scale Animation"
    )

    // Observe checkout state for navigation
    LaunchedEffect(uiState.isCheckedOut) {
        if (uiState.isCheckedOut) {
            isCheckingOut = false
            onNavigateBack()
            viewModel.onCheckoutConsumed()
        }
    }
    
    // Refresh tables when navigating to the tables screen (index 0)
    LaunchedEffect(uiState.selectedNavIndex) {
        if (uiState.selectedNavIndex == 0) {
            tablesViewModel.fetchOrders()
        }
    }

    val items = listOf("Tables", "Home", "Inventory")
    val icons = listOf(Icons.Filled.TableRestaurant, Icons.Filled.Home, Icons.Filled.Inventory2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RistoSmart") },
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
                        onClick = { viewModel.onNavBarBtnPressed(index) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (uiState.selectedNavIndex) {
            0 -> WaiterTablesScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = tablesViewModel
            )
            1 -> KitchenStaffHomeContent(
                uiState = uiState,
                isCheckingOut = isCheckingOut,
                buttonScale = buttonScale,
                onCheckoutPressed = {
                    isCheckingOut = true
                    viewModel.onCheckoutPressed()
                },
                modifier = Modifier.padding(innerPadding)
            )
            2 -> KitchenStaffInventoryScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun KitchenStaffHomeContent(
    uiState: KitchenStaffUiState,
    isCheckingOut: Boolean,
    buttonScale: Float,
    onCheckoutPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {

        Text(text = "Welcome Kitchen Staff")

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
                Text(text = uiState.status) 

                Button(
                    onClick = onCheckoutPressed,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.graphicsLayer(
                        scaleX = buttonScale,
                        scaleY = buttonScale
                    ),
                    enabled = !isCheckingOut
                ) {
                     if (isCheckingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Check Out", color = Color.Black)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Working since:")
                    Text(text = uiState.time) 
                }
            }
        }
    }
}

@Composable
fun KitchenStaffInventoryScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Inventory Screen (Placeholder)")
    }
}
