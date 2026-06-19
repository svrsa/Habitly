package com.example.habitly.ui.settings

data class SettingsUiState(
    val defaultFocusDurationMinutes: Int = DEFAULT_FOCUS_DURATION_MINUTES,
    val isDailyReminderEnabled: Boolean = false
) {
    companion object {
        const val DEFAULT_FOCUS_DURATION_MINUTES = 25
    }
}
