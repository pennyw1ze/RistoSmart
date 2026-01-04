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

    // Camera Logic
    fun onScanClicked() {
        _uiState.update { it.copy(isScanning = true, showScanBtn = false, showResults = false) }
    }

    fun onRetryClicked() {
        _uiState.update { it.copy(isScanning = true, showScanBtn = false, showResults = false) }
    }

    fun onAddClicked(productId: String,quantity: Int) {
        viewModelScope.launch {
            val result = inventoryRepository.putInventoryItem(productId, quantity)

            result.onSuccess { response ->
                _uiState.update { it.copy(
                    addedItem = false,
                    inventoryItems = response.data
                ) }
                println("ITEM SUCCESSFULLY ADDED TO DB!")
            }
            result.onFailure { e ->
                println("Error fetching add item: ${e.message}")
            }
        }
    }

    fun onAddBarcodeClicked(barcode: String, quantity: Int) {

        viewModelScope.launch {
            println("API CALL MADE")
            val result = inventoryRepository.putInventoryItemByBarcode(barcode, quantity)

            result.onSuccess { response ->
                println("ITEM SUCCESSFULLY ADDED TO DB!")

                _uiState.update { it.copy(
                    inventoryItems = response.data
                ) }

                // Close the "Add Quantity" dialog and reset
                setBarcodeItem(false)
                resetCameraState()
            }

            result.onFailure { e ->
                // --- DEBUGGING: Uncomment these lines to see the exact code in Logcat ---
                // println("Exception Type: ${e::class.simpleName}")
                if (e is retrofit2.HttpException) println("HTTP Code: ${e.code()}")

                // 1. Define what counts as "Not Found"
                val isHttpError = e is retrofit2.HttpException && (e.code() == 401 || e.code() == 404)
                val isTextError = e.message?.contains("Product not found", ignoreCase = true) == true

                if (isHttpError || isTextError) {
                    println("Item not found internally (Code: 401/404 or Message match).")

                    // 2. IMPORTANT: Close the 'Quantity' dialog first!
                    // If you don't do this, the error dialog might be blocked by the quantity dialog.
                    setBarcodeItem(false)
                    // 3. Open the 'Item Not Found' dialog
                    setFetchItemError(true)

                    // ... Your external lookup code here ...

                    val newItemResult = inventoryRepository.fetchNewItem(barcode, quantity)

                    newItemResult.onSuccess { response ->
                        println("External item found: ${response.data.firstOrNull()?.name}")

                        _uiState.update { currentState ->
                            currentState.copy(
                                // Append the newly found item(s) to the existing list
                                inventoryItems = currentState.inventoryItems + response.data,
                                // Trigger the "Added" state (optional, if you want a snackbar/toast)
                                addedItem = false,
                                // IMPORTANT: Close the "Not Found" dialog since we found it!
                            )
                        }
                    }

                    newItemResult.onFailure { lookupError ->
                        println("External lookup failed: ${lookupError.message}")
                        // We keep fetchItemError = true so the user sees the dialog
                        // saying it wasn't found internally.
                    }

                } else {
                    // Generic error handling
                    println("Error fetching barcode item: ${e.message}")

                    // Optional: Still close the quantity dialog so the user isn't stuck
                    setBarcodeItem(false)
                }
            }
        }
    }



    fun onRemoveClicked(productId: String, quantity: Int){
        viewModelScope.launch {
            val result = inventoryRepository.deleteInventoryItem(productId, quantity)

            result.onSuccess { response ->
                _uiState.update { it.copy(
                    removedItem = false,
                    inventoryItems = response.data
                ) }
                println("ITEM SUCCESSFULLY REMOVED FROM DB!")
            }
            result.onFailure { e ->
                println("Error fetching remove item: ${e.message}")
            }
        }
    }

    fun onBarcodeFound(barcode: String) {
        println("ML KIT FOUND BARCODE: $barcode")
        _uiState.update { it.copy(
            isScanning = false,
            showScanBtn = false, // Keep scan button hidden
            showResults = true,
            scannedCode = barcode
        ) }
    }


    fun setAddedItemState(newValue: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(addedItem = newValue)
        }
    }

    fun setRemovedItemState(newValue: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(removedItem = newValue)
        }
    }

    fun setSelectedItem(item: InventoryItem?) {
        _uiState.update { currentState ->
            currentState.copy(selectedItem = item)
        }
    }

    fun setBarcodeItem(barcodeItem: Boolean){
        _uiState.update { currentState ->
            currentState.copy(barcodeItem = barcodeItem)
        }
    }

    fun setFetchItemError(fetchItemError: Boolean){
        _uiState.update { currentState ->
            currentState.copy(fetchItemError = fetchItemError)
        }
    }




    fun resetCameraState() {
        _uiState.update { it.copy(
            isScanning = false,
            showScanBtn = true,
            showResults = false,
            scannedCode = ""
        ) }
    }
}

data class KitchenStaffInventoryUiState(
    val inventoryItems: List<InventoryItem> = emptyList(),
    // Camera State
    val isScanning: Boolean = false,
    val showScanBtn: Boolean = true,
    val showResults: Boolean = false,
    val scannedCode: String = "",
    val addedItem: Boolean = false,
    val itemData: List<InventoryItem> = emptyList(),
    val removedItem: Boolean = false,
    val selectedItem: InventoryItem? = null,
    val barcodeItem: Boolean = false,
    val fetchItemError: Boolean = false
)
