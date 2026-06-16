package com.example.habitly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.habitly.data.repository.SettingsRepository

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
