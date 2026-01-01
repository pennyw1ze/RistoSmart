package com.example.ristosmart.ui.screens.camera

import androidx.lifecycle.ViewModel
import com.example.ristosmart.repository.InventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CameraViewModel: ViewModel() {

    private val inventoryRepository = InventoryRepository()
    private val _uiState = MutableStateFlow(CameraUiState())
    // Public state (read-only)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()


    fun onBtnPressed(tableNumber: Int){
        println("Table $tableNumber selected")
    }

    fun onNavBarBtnPressed(id: Int) {
        _uiState.update { it.copy(selectedNavIndex = id) }
        println("Navbar item $id pressed. Handle navigation or API call here.")
    }

    fun onScanClicked() {
        _uiState.update { it.copy(isScanning = true, showScanBtn = false, showResults = false) }
    }

    fun onRetryClicked() {
        _uiState.update { it.copy(isScanning = true, showScanBtn = false, showResults = false) }
    }

    fun onBarcodeFound(barcode: String) {
        println("ML KIT FOUND BARCODE: $barcode")
        _uiState.update { it.copy(
            isScanning = false,
            showScanBtn = false, // Keep scan button hidden
            showResults = true,
            scannedCode = barcode
        ) }


        // TODO: decide what do to!
        // IF ITEM ALREADY IN DB: -> SIMPLY ADD
        // ELSE: FIND ITEM IN API? -> ADD TO DB. DATA SHOULD BE ADJUSTED ?
        //viewModelScope.launch {
        //val item = inventoryRepository.getProductByBarcode(barcode) ??
        //_uistate.update { it.copy(isScanning = false) } ??
        //}

    }
}

data class CameraUiState(
    val selectedNavIndex: Int = 0,
    val isScanning: Boolean = false,
    val showScanBtn: Boolean = true,
    val showResults: Boolean = false,
    val scannedCode: String = ""
)
