package com.example.habitly.data.repository

import com.example.habitly.ui.settings.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SettingsRepository {
    private val _settings = MutableStateFlow(SettingsUiState())
    val settings: StateFlow<SettingsUiState> = _settings

    fun selectDefaultFocusDuration(minutes: Int) {
        _settings.update { state ->
            state.copy(defaultFocusDurationMinutes = minutes)
        }
    }

    fun setDailyReminderEnabled(isEnabled: Boolean) {
        _settings.update { state ->
            state.copy(isDailyReminderEnabled = isEnabled)
        }
    }
}
