package com.example.habitly.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyTaskRepository
import com.example.habitly.data.repository.StudyEvidenceRepository

class StatisticsViewModelFactory(
    private val taskRepository: StudyTaskRepository,
    private val sessionRepository: StudySessionRepository,
    private val evidenceRepository: StudyEvidenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(
                taskRepository = taskRepository,
                sessionRepository = sessionRepository,
                evidenceRepository = evidenceRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
