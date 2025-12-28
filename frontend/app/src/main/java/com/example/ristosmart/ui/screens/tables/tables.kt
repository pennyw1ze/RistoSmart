package com.example.ristosmart.ui.screens.tables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableScreen(
    viewModel: TablesViewModel = viewModel(),
    onNavigateToHome: ()-> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToInventory: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val items = listOf("Tables", "Home", "Inventory")
    val icons = listOf(Icons.Filled.TableRestaurant, Icons.Filled.Home, Icons.Filled.Inventory2)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orders") },
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text("Table {{ change_me_from_api }}") // TODO: FETCH TABLE FROM API!
            Card(
                border = BorderStroke(1.dp, Color.Blue),
                modifier = Modifier.padding(16.dp)
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text="Menù")
                    Spacer(modifier = Modifier.padding(16.dp))

                    Text(text = "Item 1, qt: x")
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(text = "Item 2, qt: y")
                    Spacer(modifier = Modifier.padding(16.dp))
                    Text(text = "Item 3, qt: z")
                    Spacer(modifier = Modifier.padding(16.dp))
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = { viewModel.onTextFieldChanged(it) },
                        label = { Text("Notes") },
                        colors = OutlinedTextFieldDefaults.colors(
                            // The border color when the field is focused (clicked)
                            focusedBorderColor = Color.Blue,
                            // The border color when the field is not focused
                            unfocusedBorderColor = Color.Blue,
                            // The color of the label when focused
                            focusedLabelColor = Color.Blue)
                    )

                    Button(
                        onClick = { viewModel.onReadyPressed() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text(text = "Check In", color = Color.Black)
                    }
                }
            }
        }
    }
}
