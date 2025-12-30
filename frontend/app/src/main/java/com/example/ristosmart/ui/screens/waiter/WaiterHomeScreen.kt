package com.example.ristosmart.ui.screens.waiter

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ristosmart.model.MenuItem
import com.example.ristosmart.model.Order
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaiterHomeScreen(
    viewModel: WaiterViewModel = viewModel(),
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
    
    // Refresh tables when navigating to the tables screen
    LaunchedEffect(uiState.selectedNavIndex) {
        if (uiState.selectedNavIndex == 2) {
            tablesViewModel.fetchOrders()
        }
    }

    val items = listOf("Menù", "Home", "Tables")
    val icons = listOf(Icons.Filled.RestaurantMenu, Icons.Filled.Home, Icons.Filled.TableRestaurant)

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
            0 -> WaiterMenuScreen(modifier = Modifier.padding(innerPadding))
            1 -> WaiterHomeContent(
                uiState = uiState,
                isCheckingOut = isCheckingOut,
                buttonScale = buttonScale,
                onCheckoutPressed = {
                    isCheckingOut = true
                    viewModel.onCheckoutPressed()
                },
                modifier = Modifier.padding(innerPadding)
            )
            2 -> WaiterTablesScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = tablesViewModel,
                userRole = "waiter" // Explicitly pass "waiter" role here
            )
        }
    }
}

@Composable
fun WaiterHomeContent(
    uiState: WaiterUiState,
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

        Text(text = "Welcome, waiter")

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
fun WaiterMenuScreen(
    modifier: Modifier = Modifier,
    viewModel: WaiterMenuViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showOrderSummary by remember { mutableStateOf(false) }
    var showTableDialog by remember { mutableStateOf(false) }
    var tableNumber by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe order status messages
    LaunchedEffect(uiState.orderStatusMessage) {
        if (uiState.orderStatusMessage != null) {
             // In a real app we might show a snackbar here, but as per requirements
             // we are showing the message color-coded. The message display logic is handled 
             // in the UI below or we can use a transient state if we want it to disappear.
             // For now we will auto-clear the message after 3 seconds.
             delay(3000)
             viewModel.clearStatusMessage()
        }
    }

    // Define the custom order for categories
    val categoryOrder = listOf("Appetizer", "Main", "Side", "Dessert", "Beverage")

    // Group items by category and sort them according to the defined order
    val groupedItems = remember(uiState.menuItems) {
        uiState.menuItems
            .groupBy { it.category }
            .toSortedMap(compareBy { category ->
                // Capitalize the category to match the order list (API likely returns lowercase)
                val capitalizedCategory = category.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                }
                
                // Find index in the predefined list; if not found, put it at the end
                val index = categoryOrder.indexOf(capitalizedCategory)
                if (index != -1) index else Int.MAX_VALUE
            })
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp, top = 16.dp, start = 16.dp, end = 16.dp), // Add bottom padding for FAB
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Status Message Display
                    if (uiState.orderStatusMessage != null) {
                        item {
                            val isSuccess = uiState.orderStatusSuccess == true
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSuccess) 
                                        MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                                ),
                                border = BorderStroke(1.dp, 
                                    if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = uiState.orderStatusMessage!!,
                                    color = if (isSuccess) 
                                        MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    groupedItems.forEach { (category, items) ->
                        item {
                            Text(
                                text = category.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(items) { menuItem ->
                                    val quantity = uiState.orderItems[menuItem] ?: 0
                                    MenuItemCard(
                                        menuItem = menuItem,
                                        quantity = quantity,
                                        onAdd = { viewModel.addToOrder(menuItem) },
                                        onRemove = { viewModel.removeFromOrder(menuItem) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Floating Action Button to view order
                if (uiState.orderItems.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = { showOrderSummary = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Order")
                            Text("View Order (${uiState.orderItems.values.sum()})")
                        }
                    }
                }
            }
        }
    }

    if (showOrderSummary) {
        ModalBottomSheet(
            onDismissRequest = { showOrderSummary = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Current Order",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.orderItems.toList()) { (item, quantity) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = String.format(Locale.getDefault(), "€%.2f x %d", item.price, quantity),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.removeFromOrder(item) }) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Remove")
                                }
                                Text(text = quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                                IconButton(onClick = { viewModel.addToOrder(item) }) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add")
                                }
                            }
                        }
                    }
                    
                    item {
                        OutlinedTextField(
                            value = uiState.orderNote,
                            onValueChange = { viewModel.updateOrderNote(it) },
                            label = { Text("Note for Kitchen") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            minLines = 2,
                            maxLines = 4
                        )
                    }
                }

                val total = uiState.orderItems.entries.sumOf { (item, qty) -> item.price * qty }
                Text(
                    text = String.format(Locale.getDefault(), "Total: €%.2f", total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )

                Button(
                    onClick = {
                        showOrderSummary = false
                        showTableDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Proceed to Checkout")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showTableDialog) {
        AlertDialog(
            onDismissRequest = { showTableDialog = false },
            title = { Text("Enter Table Number") },
            text = {
                OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { if (it.all { char -> char.isDigit() }) tableNumber = it },
                    label = { Text("Table Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tableNum = tableNumber.toIntOrNull()
                        if (tableNum != null) {
                            viewModel.sendOrder(tableNum)
                            showTableDialog = false
                            tableNumber = ""
                        }
                    },
                    enabled = tableNumber.isNotEmpty()
                ) {
                    Text("Send Order")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTableDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .padding(bottom = 8.dp), // Add padding for shadow
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = menuItem.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "€${menuItem.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            if (!menuItem.description.isNullOrBlank()) {
                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (menuItem.allergens?.isNotEmpty() == true) {
                 Text(
                    text = "Allergens: ${menuItem.allergens.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                     maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                 if (quantity > 0) {
                     IconButton(
                         onClick = onRemove,
                         modifier = Modifier.size(32.dp)
                     ) {
                         Icon(
                             Icons.Filled.Remove, 
                             contentDescription = "Remove",
                             tint = MaterialTheme.colorScheme.primary
                         )
                     }
                     
                     Text(
                         text = quantity.toString(),
                         style = MaterialTheme.typography.bodyLarge,
                         fontWeight = FontWeight.Bold
                     )
                 } else {
                     Spacer(modifier = Modifier.width(32.dp)) // Spacer to keep layout consistent
                 }

                IconButton(
                    onClick = onAdd,
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        Icons.Filled.Add, 
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}


@Composable
fun WaiterTablesScreen(
    modifier: Modifier = Modifier,
    viewModel: WaiterTablesViewModel = viewModel(),
    userRole: String? // Pass role here to control visibility
) {
    val uiState by viewModel.uiState.collectAsState()

    // Group orders by table number
    val groupedOrders = remember(uiState.orders, uiState.filterStatus, uiState.sortByRecent) {
        var filtered = uiState.orders
        if (uiState.filterStatus != null) {
            filtered = filtered.filter { it.status.equals(uiState.filterStatus, ignoreCase = true) }
        }
        
        // Sorting Logic Fix:
        // Use sortedWith to define a custom comparator that handles nulls and order
        if (uiState.sortByRecent) {
             // Sort by createdAt descending (newest first). Handle nulls last.
             filtered = filtered.sortedByDescending { it.createdAt }
        } else {
             // Sort by tableNumber ascending. Handle nulls last.
             filtered = filtered.sortedBy { it.tableNumber ?: Int.MAX_VALUE }
        }

        // After sorting the flat list, we group it. 
        // Note: 'groupBy' preserves the order of keys as they first appear in the source list.
        // So if 'filtered' is sorted by date, the groups will appear in that order of first appearance.
        // HOWEVER, 'toSortedMap' will RE-SORT the keys (table numbers).
        // If we want to keep the "Recent" sort order for the GROUPS, we should NOT use 'toSortedMap' blindly
        // or we need a map that respects insertion order (LinkedHashMap) which 'groupBy' returns by default.
        
        if (uiState.sortByRecent) {
            // If sorting by recent, we probably want to see the tables with the most recent orders first.
            // groupBy returns a LinkedHashMap which preserves insertion order.
            // Since we sorted 'filtered' by recent, the table with the most recent order will be first key.
            filtered.groupBy { it.tableNumber ?: 0 } 
        } else {
            // If sorting by table number, we want keys 1, 2, 3...
            filtered.groupBy { it.tableNumber ?: 0 }.toSortedMap()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = uiState.error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Filter and Sort Controls
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Button(
                            onClick = { 
                                val newSort = !uiState.sortByRecent
                                viewModel.filterOrders(uiState.filterStatus, newSort) 
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.sortByRecent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (uiState.sortByRecent) "Sort: Recent" else "Sort: Table")
                        }
                    }
                    
                    item {
                        val isConfirmed = uiState.filterStatus == "confirmed"
                        Button(
                            onClick = { 
                                viewModel.filterOrders(if (isConfirmed) null else "confirmed", uiState.sortByRecent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isConfirmed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Confirmed", color = if (isConfirmed) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    item {
                        val isReady = uiState.filterStatus == "ready"
                         Button(
                            onClick = { 
                                viewModel.filterOrders(if (isReady) null else "ready", uiState.sortByRecent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isReady) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Ready", color = if (isReady) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    groupedOrders.forEach { (tableNumber, orders) ->
                        item {
                            TableCard(
                                tableNumber = tableNumber, 
                                orders = orders,
                                userRole = userRole,
                                onUpdateStatus = { orderId, newStatus ->
                                    viewModel.updateOrderStatus(orderId, newStatus)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCard(
    tableNumber: Int, 
    orders: List<Order>,
    userRole: String?,
    onUpdateStatus: (String, String) -> Unit = { _, _ -> }
) {
    val totalBill = orders.sumOf { it.finalAmount }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with Table Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Table $tableNumber",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                // You could add table status indicators here if available (e.g. occupied, paying)
            }

            Divider()

            // List of Orders for this table
            orders.forEach { order ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                         modifier = Modifier.fillMaxWidth(),
                         horizontalArrangement = Arrangement.SpaceBetween,
                         verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.orderNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                             if (!order.specialInstructions.isNullOrBlank()) {
                                Text(
                                    text = "Note: ${order.specialInstructions}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val statusColor = when(order.status) {
                                "completed" -> Color(0xFF2E7D32) // Keep specific success color or use generic
                                "cancelled" -> MaterialTheme.colorScheme.error
                                "ready" -> Color.Green // Or primary
                                else -> Color(0xFFF57C00) // Orange
                            }
                            Text(
                                text = order.status,
                                style = MaterialTheme.typography.labelMedium,
                                color = statusColor,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            
                            // Role-based logic for buttons
                            
                            // 1. Waiter Action: Ready -> Delivered
                            // Only waiter should see this button
                            if (userRole == "waiter" && order.status.equals("ready", ignoreCase = true)) {
                                Button(
                                    onClick = { onUpdateStatus(order.id, "delivered") },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("Deliver", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            
                            // 2. Kitchen Staff (Chef) Action: Confirmed -> Ready
                            // Only chef (or manager/kitchen staff) should see this button
                            // Assuming "chef" is the role name for kitchen staff based on previous context
                            if (userRole == "chef" && order.status.equals("confirmed", ignoreCase = true)) {
                                Button(
                                    onClick = { onUpdateStatus(order.id, "ready") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Green for ready
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("Ready", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    // Order Items
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                             Text(
                                 text = "${item.quantity}x ${item.menuItemName}",
                                 style = MaterialTheme.typography.bodyMedium
                             )
                             Text(
                                 text = String.format(Locale.getDefault(), "€%.2f", item.totalPrice),
                                 style = MaterialTheme.typography.bodyMedium
                             )
                        }
                    }
                    if (order != orders.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Divider()

            // Total Bill Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Bill",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format(Locale.getDefault(), "€%.2f", totalBill),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outlineVariant)
    )
}
