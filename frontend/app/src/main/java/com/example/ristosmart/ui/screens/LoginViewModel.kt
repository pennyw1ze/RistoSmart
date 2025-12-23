package com.example.ristosmart.ui.screens

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class LoginViewModel : ViewModel() {


    // Internal state (mutable)
    private val _uiState = MutableStateFlow(LoginUiState())
    // Public state (read-only)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
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

    fun login() {
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //this is not actually right since we must do API calls here
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        val email = _uiState.value.email


        // Handle your login logic here (e.g., call a Repository)
        println("Logging in with $email")
    }
}

// Data class to hold the UI state
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val forgotPressed: Boolean = false
)