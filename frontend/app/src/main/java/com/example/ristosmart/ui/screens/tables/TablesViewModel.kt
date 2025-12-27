package com.example.ristosmart.ui.screens.tables


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class TablesViewModel : ViewModel() {


    // Internal state (mutable)
    private val _uiState = MutableStateFlow(TablesUiState())

    // Public state (read-only)
    val uiState: StateFlow<TablesUiState> = _uiState.asStateFlow()

    fun onReadyPressed() {
        _uiState.update { it.copy(readypressed = true) }
    }

    fun onTextFieldChanged(text: String) {
        _uiState.update { it.copy(notes = text) }
    }

    fun onNavBarBtnPressed(id: Int) {
        _uiState.update { it.copy(selectedNavIndex = id) }
    }
}

data class TablesUiState(
    val readypressed: Boolean = false,
    val selectedNavIndex: Int = 0,
    val notes: String = ""
)
