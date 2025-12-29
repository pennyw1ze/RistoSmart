package com.example.ristosmart.ui.screens.waiter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.model.Order
import com.example.ristosmart.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WaiterTablesViewModel : ViewModel() {
    private val repository = OrderRepository()
    private val _uiState = MutableStateFlow(WaiterTablesUiState())
    val uiState: StateFlow<WaiterTablesUiState> = _uiState.asStateFlow()

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getOrders()
            result.onSuccess { orders ->
                _uiState.update { 
                    it.copy(
                        orders = orders, 
                        isLoading = false
                    ) 
                }
            }.onFailure { e ->
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to fetch orders", 
                        isLoading = false
                    ) 
                }
            }
        }
    }
}

data class WaiterTablesUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
