package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePaletteManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ra7alat_theme_settings", Context.MODE_PRIVATE)
    private val _currentPalette = MutableStateFlow(
        AppThemePalette.fromId(prefs.getString("selected_palette_id", AppThemePalette.WHITE_ROYAL.id))
    )
    val currentPalette: StateFlow<AppThemePalette> = _currentPalette.asStateFlow()

    fun setPalette(palette: AppThemePalette) {
        _currentPalette.value = palette
        prefs.edit().putString("selected_palette_id", palette.id).apply()
    }

    fun getAvailablePalettes(): List<AppThemePalette> {
        return AppThemePalette.entries
    }
}
