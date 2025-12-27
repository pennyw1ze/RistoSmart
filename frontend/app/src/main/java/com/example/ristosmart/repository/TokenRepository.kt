package com.example.ristosmart.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object TokenRepository {
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken: StateFlow<String?> = _refreshToken.asStateFlow()
    
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    fun saveTokens(accessToken: String?, refreshToken: String?, role: String?) {
        _accessToken.value = accessToken
        _refreshToken.value = refreshToken
        _userRole.value = role
    }

    fun clearTokens() {
        _accessToken.value = null
        _refreshToken.value = null
        _userRole.value = null
    }
}
