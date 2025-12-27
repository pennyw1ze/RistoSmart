package com.example.ristosmart.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ristosmart.model.User
import com.example.ristosmart.repository.AuthRepository
import com.example.ristosmart.repository.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    // Internal state (mutable)
    private val _uiState = MutableStateFlow(LoginUiState())
    // Public state (read-only)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, error = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, error = null) }
    }

    fun onRememberMeChange(newRememberMe: Boolean) {
        _uiState.update { it.copy(rememberMe = newRememberMe) }
    }

    fun onForgotPressed(newPressed: Boolean){
        _uiState.update { it.copy(forgotPressed = newPressed) }
        //go to Forgot.kt
    }

    //reset the state of forgot to false (once I go to forgot.kt, state must return to false)
    fun onForgotPressedConsumed() {
        _uiState.update { it.copy(forgotPressed = false) }
    }
    
    fun onLoginSuccessConsumed() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    fun login() {
        val username = _uiState.value.email
        val password = _uiState.value.password
        
        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Username and password required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = authRepository.login(username, password)
            
            result.onSuccess { loginResponse ->
                // Save tokens
                TokenRepository.saveTokens(
                    loginResponse.accessToken, 
                    loginResponse.refreshToken,
                    loginResponse.user?.role
                )
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        loginSuccess = true,
                        user = loginResponse.user,
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken
                    ) 
                }
            }.onFailure { exception ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Login failed"
                    ) 
                }
            }
        }
    }
}

// Data class to hold the UI state
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val forgotPressed: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false,
    val user: User? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)
