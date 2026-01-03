package com.example.ristosmart.ui.theme

import androidx.compose.runtime.mutableStateOf

object ThemeManager {
    val isDarkTheme = mutableStateOf(false)

    fun setDarkTheme(isDark: Boolean) {
        isDarkTheme.value = isDark
    }
}
