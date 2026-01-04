package com.example.ristosmart.ui.screens.manager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.model.CreateMenuItemRequest
import com.example.ristosmart.model.MenuItem
import com.example.ristosmart.model.Order
import com.example.ristosmart.model.RegisterRequest
import com.example.ristosmart.model.UpdateMenuItemRequest
import com.example.ristosmart.model.UpdateAvailabilityRequest
import com.example.ristosmart.model.User
import com.example.ristosmart.network.RetrofitClient
import com.example.ristosmart.repository.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ManagerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ManagerUiState())
    val uiState: StateFlow<ManagerUiState> = _uiState.asStateFlow()

    private val apiService = RetrofitClient.apiService

    init {
        fetchMenu()
    }

    fun onNavBarBtnPressed(index: Int) {
        _uiState.update { it.copy(selectedNavIndex = index) }
        when (index) {
            0 -> fetchMenu()
            1 -> fetchOrders()
            2 -> fetchUsers()
        }
    }

    fun logout() {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()
            if (token == null) {
                _uiState.update { it.copy(logoutSuccess = true) }
                return@launch
            }

            try {
                val authHeader = "Bearer $token"
                apiService.logout(authHeader)
            } catch (e: Exception) {
                Log.e("ManagerViewModel", "Logout failed", e)
            } finally {
                TokenRepository.clearTokens()
                _uiState.update { it.copy(logoutSuccess = true) }
            }
        }
    }

    fun onLogoutConsumed(){
        _uiState.update { it.copy(logoutSuccess = false) }
    }

    // --- Menu Management ---

    fun updateFilters(category: String?, showUnavailableOnly: Boolean) {
        _uiState.update { it.copy(selectedCategory = category, showUnavailableOnly = showUnavailableOnly) }
        fetchMenu()
    }

    private fun fetchMenu() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val category = _uiState.value.selectedCategory?.lowercase()
                val available = if (_uiState.value.showUnavailableOnly) false else null

                val response = apiService.getMenu(category, available)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(menuItems = response.body()!!.data, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun saveMenuItem(item: MenuItem, isCreating: Boolean) {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"

            try {
                val response = if (isCreating) {
                    val request = CreateMenuItemRequest(
                        name = item.name,
                        price = item.price,
                        category = item.category.lowercase(),
                        preparationTime = item.preparationTime,
                        description = item.description,
                        allergens = item.allergens,
                        isAvailable = item.isAvailable,
                        imageUrl = item.imageUrl,
                        taxAmount = item.taxAmount,
                        nutritionalInfo = item.nutritionalInfo
                    )
                    apiService.createMenuItem(authHeader, request)
                } else {
                    val request = UpdateMenuItemRequest(
                        name = item.name,
                        price = item.price,
                        category = item.category.lowercase(),
                        preparationTime = item.preparationTime,
                        description = item.description,
                        allergens = item.allergens,
                        isAvailable = item.isAvailable,
                        imageUrl = item.imageUrl,
                        taxAmount = item.taxAmount,
                        nutritionalInfo = item.nutritionalInfo
                    )
                    apiService.updateMenuItem(authHeader, item.id, request)
                }
                
                if (response.isSuccessful) {
                    fetchMenu() // Refresh list on success
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to save item: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to save item: ${e.message}") }
            }
        }
    }

    fun deleteMenuItem(item: MenuItem) {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"
            try {
                val response = apiService.deleteMenuItem(authHeader, item.id)
                if (response.isSuccessful) {
                    fetchMenu() // Refresh list
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to delete item: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete item: ${e.message}") }
            }
        }
    }

    fun toggleAvailability(item: MenuItem) {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"
            val request = UpdateAvailabilityRequest(isAvailable = !item.isAvailable)
            try {
                val response = apiService.updateMenuAvailability(authHeader, item.id, request)
                if (response.isSuccessful) {
                    fetchMenu() // Refresh list
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to update availability: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to update availability: ${e.message}") }
            }
        }
    }

    // --- Orders Management ---

    private fun fetchOrders() {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getOrders(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(orders = response.body()!!.data, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"
            try {
                val response = apiService.deleteOrder(authHeader, orderId)
                if (response.isSuccessful) {
                    fetchOrders() // Refresh list
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to delete order: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete order: ${e.message}") }
            }
        }
    }

    // --- User Management ---

    private fun fetchUsers() {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getUsers(authHeader)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(users = response.body()!!.data, isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"
            try {
                val response = apiService.deleteUser(authHeader, user.id)
                if (response.isSuccessful) {
                    fetchUsers()
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to delete user: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to delete user: ${e.message}") }
            }
        }
    }

    fun createUser(request: RegisterRequest) {
        viewModelScope.launch {
            val token = TokenRepository.accessToken.first()!!
            val authHeader = "Bearer $token"
            try {
                val response = apiService.registerUser(authHeader, request)
                if (response.isSuccessful) {
                    fetchUsers()
                } else {
                    _uiState.update { it.copy(errorMessage = "Failed to create user: ${response.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to create user: ${e.message}") }
            }
        }
    }
}

data class ManagerUiState(
    val selectedNavIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val menuItems: List<MenuItem> = emptyList(),
    val orders: List<Order> = emptyList(),
    val users: List<User> = emptyList(),
    val logoutSuccess: Boolean = false,
    val selectedCategory: String? = null,
    val showUnavailableOnly: Boolean = false
)
