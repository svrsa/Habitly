package com.example.habitly.ui.settings

data class SettingsUiState(
    val defaultFocusDurationMinutes: Int = DEFAULT_FOCUS_DURATION_MINUTES,
    val isDailyReminderEnabled: Boolean = false,
    val reminderHour: Int = DEFAULT_REMINDER_HOUR,
    val reminderMinute: Int = DEFAULT_REMINDER_MINUTE
) {
    companion object {
        const val DEFAULT_FOCUS_DURATION_MINUTES = 25
        const val DEFAULT_REMINDER_HOUR = 18
        const val DEFAULT_REMINDER_MINUTE = 0
    }
}
