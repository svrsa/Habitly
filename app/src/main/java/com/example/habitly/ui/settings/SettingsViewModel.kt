package com.example.habitly.ui.settings

import androidx.lifecycle.ViewModel
import com.example.habitly.data.repository.SettingsRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = settingsRepository.settings

    fun selectDefaultFocusDuration(minutes: Int) {
        settingsRepository.selectDefaultFocusDuration(minutes)
    }

    fun setDailyReminderEnabled(isEnabled: Boolean) {
        settingsRepository.setDailyReminderEnabled(isEnabled)
    }
}
