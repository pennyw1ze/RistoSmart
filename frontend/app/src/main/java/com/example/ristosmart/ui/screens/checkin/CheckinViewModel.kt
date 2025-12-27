package com.example.ristosmart.ui.screens.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class CheckinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CheckinUiState())
    val uiState: StateFlow<CheckinUiState> = _uiState.asStateFlow()

    init {
        // Simulate fetching data from API on init
        fetchStatus()
    }

    private fun fetchStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate API delay
            delay(1000)
            // Mock API response
            _uiState.update {
                it.copy(
                    status = "At home",
                    time = "00:00",
                    isLoading = false
                )
            }
        }
    }

    fun onNavBarBtnPressed(id: Int) {
        _uiState.update { it.copy(selectedNavIndex = id) }
        println("Navbar item $id pressed. Handle navigation or API call here.")
    }

    fun onCheckinPressed() {
        // Handle checkin logic here
        viewModelScope.launch {
            // Simulate API call
            delay(500)
            println("Checkin pressed")
        }
    }
}

data class CheckinUiState(
    val status: String = "Loading...",
    val time: String = "--:--",
    val isLoading: Boolean = false,
    val selectedNavIndex: Int = 0
)
