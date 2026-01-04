package com.example.ristosmart.ui.screens.manager

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ristosmart.model.MenuItem
import com.example.ristosmart.model.Order
import com.example.ristosmart.model.RegisterRequest
import com.example.ristosmart.model.User
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerHomeScreen(
    viewModel: ManagerViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onNavigateBack()
            viewModel.onLogoutConsumed() // Reset the flag
        }
    }

    // Removed Inventory and Check-in from items and icons
    val items = listOf("Menu", "Orders", "Users")
    val icons = listOf(
        Icons.Filled.RestaurantMenu,
        Icons.AutoMirrored.Filled.ListAlt,
        Icons.Filled.Group
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manager Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item, maxLines = 1) },
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
        Box(modifier = Modifier.padding(innerPadding)) {
            when (uiState.selectedNavIndex) {
                0 -> ManagerMenuScreen(viewModel = viewModel)
                1 -> ManagerOrdersScreen(viewModel = viewModel)
                2 -> ManagerUsersScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ManagerMenuScreen(viewModel: ManagerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MenuItem?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Appetizer", "Main", "Dessert", "Beverage", "Side")

    // Filter Logic: Client-side search, but server-side category/availability
    val filteredItems = uiState.menuItems.filter { item ->
        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                (item.description?.contains(searchQuery, ignoreCase = true) == true)
        
        matchesSearch
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search and Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search by name or description") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = (uiState.selectedCategory == category) || (uiState.selectedCategory == null && category == "All")
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                val newCategory = if (category == "All") null else category
                                viewModel.updateFilters(newCategory, uiState.showUnavailableOnly)
                            },
                            label = { Text(category) }
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.errorMessage ?: "Error", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredItems) { item ->
                        ManagerMenuItemCard(
                            item = item,
                            onEdit = {
                                itemToEdit = item
                                showDialog = true
                            },
                            onDelete = { viewModel.deleteMenuItem(item) },
                            onToggleAvailability = { viewModel.toggleAvailability(item) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                itemToEdit = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Item")
        }
    }

    if (showDialog) {
        MenuItemDialog(
            item = itemToEdit,
            onDismiss = { showDialog = false },
            onSave = { newItem, isCreating ->
                viewModel.saveMenuItem(newItem, isCreating)
                showDialog = false
            }
        )
    }
}

@Composable
fun ManagerMenuItemCard(
    item: MenuItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleAvailability: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.category.replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // Restored Switch for availability
                Switch(
                    checked = item.isAvailable,
                    onCheckedChange = { onToggleAvailability() }
                )
            }

            Text(
                text = item.description ?: "No description",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "€${item.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (item.allergens?.isNotEmpty() == true) {
                Text(
                    text = "Allergens: ${item.allergens.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDialog(
    item: MenuItem?,
    onDismiss: () -> Unit,
    onSave: (MenuItem, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var price by remember { mutableStateOf(item?.price?.toString() ?: "") }
    var category by remember { 
        mutableStateOf(item?.category?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "Appetizer") 
    }
    var prepTime by remember { mutableStateOf(item?.preparationTime?.toString() ?: "15") }
    var imageUrl by remember { mutableStateOf(item?.imageUrl ?: "") }
    var taxAmount by remember { mutableStateOf(item?.taxAmount?.toString() ?: "") }

    // Allergens State
    val availableAllergens = listOf("gluten", "dairy", "nuts", "eggs", "soy", "shellfish", "fish")
    val selectedAllergens = remember { mutableStateOf(item?.allergens?.toMutableSet() ?: mutableSetOf()) }

    var expandedCategory by remember { mutableStateOf(false) }
    val categories = listOf("Appetizer", "Main", "Side", "Dessert", "Beverage")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Create Menu Item" else "Edit Menu Item") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) price = it },
                            label = { Text("Price (€) *") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        OutlinedTextField(
                            value = prepTime,
                            onValueChange = { if (it.all { char -> char.isDigit() }) prepTime = it },
                            label = { Text("Time (min) *") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                
                item {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = taxAmount,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) taxAmount = it },
                        label = { Text("Tax Amount (€)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            value = category,
                            onValueChange = {},
                            label = { Text("Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false },
                        ) {
                            categories.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        category = selectionOption
                                        expandedCategory = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Allergens", style = MaterialTheme.typography.titleSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(availableAllergens) { allergen ->
                            val isSelected = selectedAllergens.value.contains(allergen)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) selectedAllergens.value.remove(allergen)
                                    else selectedAllergens.value.add(allergen)
                                },
                                label = { Text(allergen) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Filled.Check, contentDescription = null) }
                                } else null
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceVal = price.toDoubleOrNull()
                    val timeVal = prepTime.toIntOrNull()
                    val taxVal = taxAmount.toDoubleOrNull()
                    
                    if (name.isNotBlank() && description.isNotBlank() && priceVal != null && timeVal != null) {
                        val isCreating = item == null
                        val newItem = MenuItem(
                            id = item?.id ?: UUID.randomUUID().toString(), // Use existing ID or generate a new one for creation
                            name = name,
                            description = description,
                            price = priceVal,
                            category = category,
                            preparationTime = timeVal,
                            isAvailable = item?.isAvailable ?: true,
                            allergens = selectedAllergens.value.toList(),
                            imageUrl = if (imageUrl.isBlank()) null else imageUrl,
                            taxAmount = taxVal
                        )
                        onSave(newItem, isCreating)
                    }
                },
                enabled = name.isNotBlank() && description.isNotBlank() && price.isNotBlank() && prepTime.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- Orders Section ---

@Composable
fun ManagerOrdersScreen(viewModel: ManagerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var orderToDelete by remember { mutableStateOf<Order?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "An error occurred",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.orders) { order ->
                    ManagerOrderCard(order = order, onDelete = { orderToDelete = order })
                }
            }
        }
    }

    if (orderToDelete != null) {
        AlertDialog(
            onDismissRequest = { orderToDelete = null },
            title = { Text("Delete Order") },
            text = { Text("Are you sure you want to permanently delete order #${orderToDelete!!.orderNumber}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOrder(orderToDelete!!.id)
                        orderToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ManagerOrderCard(order: Order, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Table ${order.tableNumber ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Order", tint = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Status:")
                Text(order.status, color = when (order.status) {
                    "ready" -> Color(0xFF2E7D32)
                    "delivered" -> MaterialTheme.colorScheme.primary
                    "cancelled" -> MaterialTheme.colorScheme.error
                    else -> Color(0xFFF57C00)
                })
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total:", fontWeight = FontWeight.Bold)
                Text("€${String.format(Locale.getDefault(), "%.2f", order.finalAmount)}", fontWeight = FontWeight.Bold)
            }

            if (order.items.isNotEmpty()) {
                Column {
                    order.items.forEach {
                        Text("${it.quantity}x ${it.menuItemName}")
                    }
                }
            }
        }
    }
}

// --- Users Section ---

@Composable
fun ManagerUsersScreen(viewModel: ManagerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var userToDelete by remember { mutableStateOf<User?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null) {
             Text(
                text = uiState.errorMessage ?: "An error occurred",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else {
             LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.users) { user ->
                    ManagerUserCard(user = user, onDelete = { userToDelete = user })
                }
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add User")
        }
    }

    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete user ${userToDelete!!.username}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(userToDelete!!)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCreateDialog) {
        CreateUserDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { request ->
                viewModel.createUser(request)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun ManagerUserCard(user: User, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(
                        text = user.fullName ?: user.username,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.role.replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, shape = MaterialTheme.shapes.small)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete User", tint = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Username: ${user.username}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(onDismiss: () -> Unit, onSave: (RegisterRequest) -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("waiter") }
    var expandedRole by remember { mutableStateOf(false) }

    val roles = listOf("waiter", "chef", "manager")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New User") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = username, 
                    onValueChange = { username = it }, 
                    label = { Text("Username *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email, 
                    onValueChange = { email = it }, 
                    label = { Text("Email *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password, 
                    onValueChange = { password = it }, 
                    label = { Text("Password *") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fullName, 
                    onValueChange = { fullName = it }, 
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expandedRole,
                    onExpandedChange = { expandedRole = !expandedRole },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        readOnly = true,
                        value = role,
                        onValueChange = {},
                        label = { Text("Role *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRole,
                        onDismissRequest = { expandedRole = false },
                    ) {
                        roles.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    role = selectionOption
                                    expandedRole = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank()) {
                         val request = RegisterRequest(
                            username = username,
                            email = email,
                            password = password,
                            fullName = fullName,
                            role = role
                        )
                        onSave(request)
                    }
                },
                enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
