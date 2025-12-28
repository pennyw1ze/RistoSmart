package com.example.ristosmart.ui.screens.waiter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.model.MenuItem
import com.example.ristosmart.model.OrderItemRequest
import com.example.ristosmart.repository.MenuRepository
import com.example.ristosmart.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WaiterMenuViewModel : ViewModel() {
    private val repository = MenuRepository()
    private val orderRepository = OrderRepository()
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

    fun updateOrderNote(note: String) {
        _uiState.update { it.copy(orderNote = note) }
    }
    
    fun sendOrder(tableNumber: Int) {
        val currentState = _uiState.value
        if (currentState.orderItems.isEmpty()) return

        _uiState.update { it.copy(isLoading = true, orderStatusMessage = null, orderStatusSuccess = null) }

        val orderItemsRequest = currentState.orderItems.map { (menuItem, quantity) ->
            OrderItemRequest(menuItemId = menuItem.id, quantity = quantity)
        }

        viewModelScope.launch {
            val result = orderRepository.createOrder(
                items = orderItemsRequest,
                tableNumber = tableNumber,
                specialInstructions = if (currentState.orderNote.isNotBlank()) currentState.orderNote else null
            )
            
            result.onSuccess {
                _uiState.update { state -> 
                    state.copy(
                        isLoading = false,
                        orderItems = emptyMap(),
                        orderNote = "",
                        orderStatusMessage = "Order sent successfully!",
                        orderStatusSuccess = true
                    ) 
                }
            }.onFailure { e ->
                _uiState.update { state -> 
                    state.copy(
                        isLoading = false,
                        orderStatusMessage = "Error sending order: ${e.message}",
                        orderStatusSuccess = false
                    ) 
                }
            }
        }
    }
    
    fun clearStatusMessage() {
         _uiState.update { it.copy(orderStatusMessage = null, orderStatusSuccess = null) }
    }
}

data class WaiterMenuUiState(
    val menuItems: List<MenuItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val orderItems: Map<MenuItem, Int> = emptyMap(), // Map of MenuItem to Quantity
    val orderNote: String = "",
    val orderStatusMessage: String? = null,
    val orderStatusSuccess: Boolean? = null
)
