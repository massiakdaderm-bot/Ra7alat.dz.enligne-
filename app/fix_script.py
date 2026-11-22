import os

# 1. ThemePaletteManager.kt
with open('app/src/main/java/com/example/util/ThemePaletteManager.kt', 'w') as f:
    f.write('''package com.example.util

import android.content.Context
import android.content.SharedPreferences
import com.example.ui.theme.AppThemePalette
import kotlinx.coroutines.flow.FlowKt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemePaletteManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ra7alat_theme_settings", Context.MODE_PRIVATE)
    private val _currentPalette = MutableStateFlow(
        AppThemePalette.fromId(prefs.getString("selected_palette_id", AppThemePalette.EMERALD.id))
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
''')

# 2. AppThemePalette.kt
with open('app/src/main/java/com/example/ui/theme/AppThemePalette.kt', 'w') as f:
    f.write('''package com.example.ui.theme

import androidx.compose.ui.graphics.Color

enum class AppThemePalette(
    val id: String,
    val nameAr: String,
    val nameFr: String,
    val descriptionAr: String,
    val descriptionFr: String,
    val primaryColor: Color,
    val primaryLight: Color,
    val primaryDark: Color,
    val containerColor: Color,
    val backgroundColor: Color,
    val accentColor: Color,
    val accentContainer: Color,
    val emoji: String
) {
    EMERALD(
        id = "EMERALD",
        nameAr = "زمرد الجزائر الأخضر",
        nameFr = "Émeraude d'Algérie",
        descriptionAr = "طبيعة الأوراس والواحات الجزائرية الغناء",
        descriptionFr = "La nature verdoyante des Aurès et des oasis",
        primaryColor = Color(0xFF0D7A57),
        primaryLight = Color(0xFF139A6E),
        primaryDark = Color(0xFF095A40),
        containerColor = Color(0xFFD6F5E8),
        backgroundColor = Color(0xFFF4FAF7),
        accentColor = Color(0xFFE5A93C),
        accentContainer = Color(0xFFFDF3D6),
        emoji = "🌿"
    ),
    MEDITERRANEAN(
        id = "MEDITERRANEAN",
        nameAr = "أزرق المتوسط الفيروزي",
        nameFr = "Bleu Méditerranée",
        descriptionAr = "سواحل وهران، عنابة، جيجل وتيبازة الساحرة",
        descriptionFr = "Les côtes magiques d'Oran, Annaba et Jijel",
        primaryColor = Color(0xFF0277BD),
        primaryLight = Color(0xFF039BE5),
        primaryDark = Color(0xFF01579B),
        containerColor = Color(0xFFE1F5FE),
        backgroundColor = Color(0xFFF0F9FF),
        accentColor = Color(0xFFFF8F00),
        accentContainer = Color(0xFFFFECB3),
        emoji = "🌊"
    ),
    SAHARA_GOLD(
        id = "SAHARA_GOLD",
        nameAr = "ذهب الصحراء الكبرى",
        nameFr = "Or du Sahara",
        descriptionAr = "كثبان تاغيت وسحر تيميمون وطاسيلي ناجر",
        descriptionFr = "Dunes de Taghit et magie de Timimoun",
        primaryColor = Color(0xFFD97706),
        primaryLight = Color(0xFFF59E0B),
        primaryDark = Color(0xFFB45309),
        containerColor = Color(0xFFFEF3C7),
        backgroundColor = Color(0xFFFFFBEB),
        accentColor = Color(0xFF0D9488),
        accentContainer = Color(0xFFCCFBF1),
        emoji = "🏜️"
    ),
    SUNSET_CORAL(
        id = "SUNSET_CORAL",
        nameAr = "مرجان غروب الساحل",
        nameFr = "Corail Coucher de Soleil",
        descriptionAr = "ألوان غروب الشمس على شواطئ القالة وبجاية",
        descriptionFr = "Coucher de soleil sur les plages d'El Kala",
        primaryColor = Color(0xFFE11D48),
        primaryLight = Color(0xFFF43F5E),
        primaryDark = Color(0xFFBE123C),
        containerColor = Color(0xFFFFE4E6),
        backgroundColor = Color(0xFFFFF1F2),
        accentColor = Color(0xFFF59E0B),
        accentContainer = Color(0xFFFEF3C7),
        emoji = "🌅"
    ),
    ROYAL_PURPLE(
        id = "ROYAL_PURPLE",
        nameAr = "أرجواني ملكي نوميدي",
        nameFr = "Pourpre Royal Numide",
        descriptionAr = "أصالة سيرتا وتاريخ نوميديا العريق",
        descriptionFr = "L'authenticité de Cirta et l'histoire de la Numidie",
        primaryColor = Color(0xFF7C3AED),
        primaryLight = Color(0xFF8B5CF6),
        primaryDark = Color(0xFF6D28D9),
        containerColor = Color(0xFFEDE9FE),
        backgroundColor = Color(0xFFF5F3FF),
        accentColor = Color(0xFF10B981),
        accentContainer = Color(0xFFD1FAE5),
        emoji = "👑"
    );

    companion object {
        fun fromId(id: String?): AppThemePalette {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: EMERALD
        }
    }
}
''')

# 3. AlgeriaConstants.kt
with open('app/src/main/java/com/example/util/AlgeriaConstants.kt', 'r') as f:
    text = f.read()

if 'fun cleanWilayaName' not in text:
    clean_func = '\n    fun cleanWilayaName(raw: String): String {\n        return raw.replace(Regex("^[0-9]+ - "), "").trim()\n    }\n}\n'
    text = text.rstrip()
    if text.endswith('}'):
        text = text[:-1] + clean_func
        with open('app/src/main/java/com/example/util/AlgeriaConstants.kt', 'w') as f:
            f.write(text)

print('Updated theme, palette, and constants.')
