package com.example.habitly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun selectDefaultFocusDuration(minutes: Int) {
        viewModelScope.launch {
            settingsRepository.selectDefaultFocusDuration(minutes)
        }
    }

    fun setDailyReminderEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDailyReminderEnabled(isEnabled)
        }
    }
}
