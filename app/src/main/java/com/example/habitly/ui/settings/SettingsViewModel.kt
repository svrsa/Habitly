package com.example.habitly.ui.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    fun selectDefaultFocusDuration(minutes: Int) {
        _uiState.update { state ->
            state.copy(defaultFocusDurationMinutes = minutes)
        }
    }

    fun setDailyReminderEnabled(isEnabled: Boolean) {
        _uiState.update { state ->
            state.copy(isDailyReminderEnabled = isEnabled)
        }
    }
}
