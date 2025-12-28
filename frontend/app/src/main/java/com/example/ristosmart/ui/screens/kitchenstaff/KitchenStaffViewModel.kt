package com.example.ristosmart.ui.screens.kitchenstaff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class KitchenStaffViewModel : ViewModel() {

    // Default nav index 1 (Home) for the center
    private val _uiState = MutableStateFlow(KitchenStaffUiState(selectedNavIndex = 1))
    val uiState: StateFlow<KitchenStaffUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTimeMillis: Long = 0

    init {
        // Simulate fetching data from API on init
        startWork()
    }

    private fun startWork() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate API delay
            delay(1000)
            
            // In a real app, this start time would come from the backend (when the user checked in)
            // For now, we assume work starts "now"
            startTimeMillis = System.currentTimeMillis()

            _uiState.update {
                it.copy(
                    status = "At work",
                    isLoading = false
                )
            }
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val duration = System.currentTimeMillis() - startTimeMillis
                val formattedTime = formatDuration(duration)
                _uiState.update { it.copy(time = formattedTime) }
                delay(1000)
            }
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun onNavBarBtnPressed(id: Int) {
        _uiState.update { it.copy(selectedNavIndex = id) }
        println("Navbar item $id pressed. Handle navigation or API call here.")

    }

    fun onCheckoutPressed() {
        // Stop the chronograph
        timerJob?.cancel()
        
        viewModelScope.launch {
            // Simulate API call
            delay(500)
            // Trigger navigation back to check-in
             _uiState.update { it.copy(isCheckedOut = true) }
        }
    }
    
    fun onCheckoutConsumed() {
        _uiState.update { it.copy(isCheckedOut = false) }
    }
}

data class KitchenStaffUiState(
    val status: String = "Loading...",
    val time: String = "00:00:00",
    val isLoading: Boolean = false,
    val selectedNavIndex: Int = 1, // Default to center (Home)
    val isCheckedOut: Boolean = false
)
