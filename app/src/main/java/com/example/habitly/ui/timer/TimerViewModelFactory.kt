package com.example.habitly.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.habitly.data.repository.SettingsRepository
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyPlanRepository
import com.example.habitly.data.repository.StudyTaskRepository
import com.example.habitly.ui.planner.PlannedFocusRequest

class TimerViewModelFactory(
    private val sessionRepository: StudySessionRepository,
    private val planRepository: StudyPlanRepository,
    private val taskRepository: StudyTaskRepository,
    private val settingsRepository: SettingsRepository,
    private val plannedFocusRequest: PlannedFocusRequest?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(
                sessionRepository = sessionRepository,
                planRepository = planRepository,
                taskRepository = taskRepository,
                settingsRepository = settingsRepository,
                plannedFocusRequest = plannedFocusRequest
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
