package com.example.ristosmart.ui.screens.kitchenstaff

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ristosmart.repository.TokenRepository
import com.example.ristosmart.ui.screens.waiter.WaiterTablesScreen
import com.example.ristosmart.ui.screens.waiter.WaiterTablesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenStaffHomeScreen(
    viewModel: KitchenStaffViewModel = viewModel(),
    tablesViewModel: WaiterTablesViewModel = viewModel(),
    inventoryViewModel: KitchenStaffInventoryViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    initialTabIndex: Int? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // State for button animation
    var isCheckingOut by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (isCheckingOut) 0.95f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "Button Scale Animation"
    )

    // Handle initial tab index if provided
    LaunchedEffect(initialTabIndex) {
        if (initialTabIndex != null) {
            viewModel.onNavBarBtnPressed(initialTabIndex)
        }
    }

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
        // Refresh inventory when navigating to the inventory screen (index 2)
        if (uiState.selectedNavIndex == 2) {
             inventoryViewModel.fetchInventory()
        }
    }

    val items = listOf("Tables", "Home", "Inventory")
    val icons = listOf(Icons.Filled.TableRestaurant, Icons.Filled.Home, Icons.Filled.Inventory2)
    val role = TokenRepository.userRole.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("RistoSmart") },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
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
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (uiState.selectedNavIndex) {
            0 -> WaiterTablesScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = tablesViewModel,
                userRole = role // Pass the role
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
            2 -> KitchenStaffInventoryScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = inventoryViewModel,
                onNavigateToCamera = onNavigateToCamera
            )
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
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.graphicsLayer(
                        scaleX = buttonScale,
                        scaleY = buttonScale
                    ),
                    enabled = !isCheckingOut
                ) {
                     if (isCheckingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onError,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Check Out", color = MaterialTheme.colorScheme.onError)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenStaffInventoryScreen(
    viewModel: KitchenStaffInventoryViewModel = viewModel(),
    onNavigateToCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // No Scaffold or BottomBar here, as it will be embedded

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Current stock",
                style = MaterialTheme.typography.titleLarge
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.inventoryItems) { item ->
                    Card(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ){
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                //text should be in the start and in the end of the row
                                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Price: ${item.price}", style = MaterialTheme.typography.bodyMedium)
                            }

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = item.description ?: "", style = MaterialTheme.typography.bodySmall)
                            }
                            
                            Text("Category: ${item.category}", style = MaterialTheme.typography.bodySmall)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Button(
                                    onClick = { /* TODO: Add remove logic (PUT TO API) */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    )
                                ) {
                                    Text("remove quantity")
                                }

                                Button(
                                    onClick = { /* TODO: Add add logic (PUT TO API) */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text("add quantity")
                                }

                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                onNavigateToCamera()
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = "Camera")
        }
    }
}
