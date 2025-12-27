package com.example.ristosmart.ui.screens.orders

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OrdersViewModel : ViewModel(){
    // Internal state (mutable)
    private val _uiState = MutableStateFlow(OrdersUiState())
    // Public state (read-only)
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    fun onBtnPressed(tableNumber: Int){
        println("Table $tableNumber selected")
    }

    fun onNavBarBtnPressed(id: Int) {
        _uiState.update { it.copy(selectedNavIndex = id) }
        println("Navbar item $id pressed. Handle navigation or API call here.")
    }
}

data class OrdersUiState(
    val selectedNavIndex: Int = 0
)

