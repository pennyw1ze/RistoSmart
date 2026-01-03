package com.example.ristosmart.ui.screens.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.repository.TokenRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.location.Location

class CheckinViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CheckinUiState())
    val uiState: StateFlow<CheckinUiState> = _uiState.asStateFlow()

    // Example fixed coordinates (e.g., center of Rome)
    // Replace these with your actual restaurant coordinates
    private val RESTAURANT_LAT = 41.9028
    private val RESTAURANT_LNG = 12.4964
    private val ALLOWED_DISTANCE_METERS = 100.0f // 100 meters radius

    init {
        // Simulate fetching data from API on init
        fetchStatus()
        fetchUserRole()
    }

    private fun fetchUserRole() {
        val role = TokenRepository.userRole.value
        _uiState.update { it.copy(userRole = role ?: "Unknown") }
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

    fun verifyLocation(currentLocation: Location?): Boolean {
        if (currentLocation == null) return false
        
        val results = FloatArray(1)
        Location.distanceBetween(
            currentLocation.latitude,
            currentLocation.longitude,
            RESTAURANT_LAT,
            RESTAURANT_LNG,
            results
        )
        
        val distanceInMeters = results[0]
        return distanceInMeters <= ALLOWED_DISTANCE_METERS
    }
    
    fun onLocationVerified() {
         _uiState.update { it.copy(isLocationVerified = true) }
    }
    
    fun onLocationVerifyConsumed() {
         _uiState.update { it.copy(isLocationVerified = false) }
    }
}

data class CheckinUiState(
    val status: String = "Loading...",
    val time: String = "--:--",
    val isLoading: Boolean = false,
    val selectedNavIndex: Int = 0,
    val userRole: String = "",
    val isLocationVerified: Boolean = false
)
