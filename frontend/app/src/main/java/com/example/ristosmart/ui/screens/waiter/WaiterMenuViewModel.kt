package com.example.ristosmart.ui.screens.waiter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.model.MenuItem
import com.example.ristosmart.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WaiterMenuViewModel : ViewModel() {
    private val repository = MenuRepository()
    private val _uiState = MutableStateFlow(WaiterMenuUiState())
    val uiState: StateFlow<WaiterMenuUiState> = _uiState.asStateFlow()

    init {
        fetchMenu()
    }

    fun fetchMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getMenu()
            result.onSuccess { items ->
                _uiState.update {
                    it.copy(
                        menuItems = items,
                        isLoading = false
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to fetch menu",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun addToOrder(menuItem: MenuItem) {
        _uiState.update { currentState ->
            val currentOrder = currentState.orderItems.toMutableMap()
            val currentQuantity = currentOrder[menuItem] ?: 0
            currentOrder[menuItem] = currentQuantity + 1
            currentState.copy(orderItems = currentOrder)
        }
    }

    fun removeFromOrder(menuItem: MenuItem) {
        _uiState.update { currentState ->
            val currentOrder = currentState.orderItems.toMutableMap()
            val currentQuantity = currentOrder[menuItem] ?: 0
            if (currentQuantity > 1) {
                currentOrder[menuItem] = currentQuantity - 1
            } else {
                currentOrder.remove(menuItem)
            }
            currentState.copy(orderItems = currentOrder)
        }
    }
}

data class WaiterMenuUiState(
    val menuItems: List<MenuItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderItems: Map<MenuItem, Int> = emptyMap() // Map of MenuItem to Quantity
)
