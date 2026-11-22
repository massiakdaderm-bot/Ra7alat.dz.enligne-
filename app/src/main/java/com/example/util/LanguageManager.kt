package com.example.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ARABIC("AR", "العربية", "🇩🇿"),
    FRENCH("FR", "Français", "🇫🇷")
}

class LanguageManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ra7alat_settings", Context.MODE_PRIVATE)

    private val _currentLanguage: MutableStateFlow<AppLanguage> = MutableStateFlow(
        if (prefs.getString("app_language", "AR") == "FR") AppLanguage.FRENCH else AppLanguage.ARABIC
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isFirstLaunch: MutableStateFlow<Boolean> = MutableStateFlow(
        prefs.getBoolean("is_first_launch_lang", true)
    )
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs.edit().putString("app_language", language.code).apply()
    }

    fun markFirstLaunchComplete() {
        _isFirstLaunch.value = false
        prefs.edit().putBoolean("is_first_launch_lang", false).apply()
    }

    fun toggleLanguage(): AppLanguage {
        val next = if (_currentLanguage.value == AppLanguage.ARABIC) AppLanguage.FRENCH else AppLanguage.ARABIC
        setLanguage(next)
        return next
    }
}
