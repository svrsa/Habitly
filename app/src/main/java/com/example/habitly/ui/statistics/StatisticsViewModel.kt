package com.example.habitly.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyTaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(
    taskRepository: StudyTaskRepository,
    sessionRepository: StudySessionRepository
) : ViewModel() {
    val uiState: StateFlow<StatisticsUiState> =
        combine(
            taskRepository.allTasks,
            sessionRepository.allSessions
        ) { tasks, sessions ->
            val completedTasks = tasks.count { task -> task.isCompleted }

            StatisticsUiState(
                totalTasks = tasks.size,
                completedTasks = completedTasks,
                openTasks = tasks.size - completedTasks,
                totalSessions = sessions.size,
                totalFocusMinutes = sessions.sumOf { session -> session.durationMinutes }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState()
        )
}
