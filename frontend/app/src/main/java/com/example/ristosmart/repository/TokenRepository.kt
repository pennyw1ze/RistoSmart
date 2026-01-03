package com.example.ristosmart.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.ristosmart.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

object TokenRepository {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER = "user"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_REMEMBER_ME = "remember_me"
    private const val KEY_SAVED_USERNAME = "saved_username"

    private lateinit var sharedPreferences: SharedPreferences

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _refreshToken = MutableStateFlow<String?>(null)
    val refreshToken: StateFlow<String?> = _refreshToken.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _accessToken.value = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        _refreshToken.value = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
        _userRole.value = sharedPreferences.getString(KEY_USER_ROLE, null)
    }

    fun saveTokens(accessToken: String?, refreshToken: String?, user: User?) {
        _accessToken.value = accessToken
        _refreshToken.value = refreshToken
        _userRole.value = user?.role

        val userJson = if (user != null) {
            Json.encodeToString(User.serializer(), user)
        } else {
            null
        }

        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ROLE, user?.role)
            putString(KEY_USER, userJson)
            apply()
        }
    }

    fun clearTokens() {
        _accessToken.value = null
        _refreshToken.value = null
        _userRole.value = null

        sharedPreferences.edit().apply {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_USER_ROLE)
            remove(KEY_USER)
            apply()
        }
    }

    fun saveRememberMe(rememberMe: Boolean, username: String) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            if (rememberMe) {
                putString(KEY_SAVED_USERNAME, username)
            } else {
                remove(KEY_SAVED_USERNAME)
            }
            apply()
        }
    }

    fun getRememberMe(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun getSavedUsername(): String? {
        return sharedPreferences.getString(KEY_SAVED_USERNAME, "")
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString(KEY_USER, null) ?: return null
        return try {
            Json.decodeFromString(User.serializer(), userJson)
        } catch (e: Exception) {
            null
        }
    }

    fun getAccessToken(): String? {
        return _accessToken.value
    }
}