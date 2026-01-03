package com.example.ristosmart.ui.screens.kitchenstaff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.model.InventoryItem
import com.example.ristosmart.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class KitchenStaffInventoryViewModel : ViewModel() {

    private val inventoryRepository = InventoryRepository()
    private val _uiState = MutableStateFlow(KitchenStaffInventoryUiState())
    // Public state (read-only)
    val uiState: StateFlow<KitchenStaffInventoryUiState> = _uiState.asStateFlow()

    init {
        fetchInventory()
    }

    fun fetchInventory() {
        viewModelScope.launch {
            val result = inventoryRepository.getInventory()

            result.onSuccess { response ->
                _uiState.update { it.copy(inventoryItems = response.data) }
            }
            result.onFailure { e ->
                println("Error fetching inventory: ${e.message}")
            }
        }
    }
}

data class KitchenStaffInventoryUiState(
    val inventoryItems: List<InventoryItem> = emptyList()
)
