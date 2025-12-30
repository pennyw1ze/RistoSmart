package com.example.ristosmart.ui.screens.inventory

import androidx.lifecycle.ViewModel
import com.example.ristosmart.repository.InventoryRepository
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.model.InventoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InventoryViewModel: ViewModel() {

    private val inventoryRepository = InventoryRepository()
    private val _uiState = MutableStateFlow(InventoryUiState())
    // Public state (read-only)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
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

    fun onBtnPressed(tableNumber: Int){
        println("Table $tableNumber selected")
    }

    fun onNavBarBtnPressed(id: Int) {
        _uiState.update { it.copy(selectedNavIndex = id) }
        println("Navbar item $id pressed. Handle navigation or API call here.")
    }

}

data class InventoryUiState(
    val selectedNavIndex: Int = 0,
    val inventoryItems: List<InventoryItem> = emptyList()
)
