package com.example.habitly.ui.settings

data class SettingsUiState(
    val defaultFocusDurationMinutes: Int = 25,
    val isDailyReminderEnabled: Boolean = false
)
