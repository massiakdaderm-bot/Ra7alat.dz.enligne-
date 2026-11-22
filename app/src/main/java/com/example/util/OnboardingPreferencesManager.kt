package com.example.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingPreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ra7alat_onboarding_prefs", Context.MODE_PRIVATE)

    // 1. Welcome slides status
    private val _isWelcomeCompleted = MutableStateFlow(
        prefs.getBoolean("is_welcome_completed", false)
    )
    val isWelcomeCompleted: StateFlow<Boolean> = _isWelcomeCompleted.asStateFlow()

    // 2. User survey questions status
    private val _isSurveyCompleted = MutableStateFlow(
        prefs.getBoolean("is_survey_completed", false)
    )
    val isSurveyCompleted: StateFlow<Boolean> = _isSurveyCompleted.asStateFlow()

    // 3. Coachmarks guided tour status
    private val _isCoachmarksCompleted = MutableStateFlow(
        prefs.getBoolean("is_coachmarks_completed", false)
    )
    val isCoachmarksCompleted: StateFlow<Boolean> = _isCoachmarksCompleted.asStateFlow()

    // Survey answers getters
    val userGender: String get() = prefs.getString("survey_gender", "") ?: ""
    val userAgeGroup: String get() = prefs.getString("survey_age_group", "") ?: ""
    val userHeardFrom: String get() = prefs.getString("survey_heard_from", "") ?: ""
    val userInterests: Set<String> get() = prefs.getStringSet("survey_interests", emptySet()) ?: emptySet()
    val termsAccepted: Boolean get() = prefs.getBoolean("survey_terms_accepted", false)

    fun completeWelcome() {
        _isWelcomeCompleted.value = true
        prefs.edit().putBoolean("is_welcome_completed", true).apply()
    }

    fun completeSurvey(
        gender: String,
        ageGroup: String,
        heardFrom: String,
        interests: Set<String>,
        termsAccepted: Boolean
    ) {
        _isSurveyCompleted.value = true
        prefs.edit()
            .putBoolean("is_survey_completed", true)
            .putString("survey_gender", gender)
            .putString("survey_age_group", ageGroup)
            .putString("survey_heard_from", heardFrom)
            .putStringSet("survey_interests", interests)
            .putBoolean("survey_terms_accepted", termsAccepted)
            .apply()
    }

    fun completeCoachmarks() {
        _isCoachmarksCompleted.value = true
        prefs.edit().putBoolean("is_coachmarks_completed", true).apply()
    }

    fun resetAllForTesting() {
        prefs.edit().clear().apply()
        _isWelcomeCompleted.value = false
        _isSurveyCompleted.value = false
        _isCoachmarksCompleted.value = false
    }

    fun restartCoachmarks() {
        _isCoachmarksCompleted.value = false
        prefs.edit().putBoolean("is_coachmarks_completed", false).apply()
    }

    fun restartSurvey() {
        _isSurveyCompleted.value = false
        prefs.edit().putBoolean("is_survey_completed", false).apply()
    }
}
