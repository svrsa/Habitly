package com.example.habitly.ui.taskdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitly.data.local.entity.StudyTaskEntity
import com.example.habitly.data.local.entity.TaskPriority
import com.example.habitly.data.repository.StudySessionRepository
import com.example.habitly.data.repository.StudyTaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    taskId: Long,
    private val taskRepository: StudyTaskRepository,
    sessionRepository: StudySessionRepository
) : ViewModel() {
    val uiState: StateFlow<TaskDetailUiState> =
        combine(
            taskRepository.getTaskById(taskId),
            sessionRepository.getSessionsForTask(taskId)
        ) { task, sessions ->
            val sessionSummaries = sessions.map { session ->
                TaskSessionSummary(
                    id = session.id,
                    durationMinutes = session.durationMinutes,
                    completedAt = session.completedAt
                )
            }

            TaskDetailUiState(
                task = task,
                sessions = sessionSummaries,
                totalFocusMinutes = sessionSummaries.sumOf { session -> session.durationMinutes },
                sessionCount = sessionSummaries.size
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskDetailUiState()
        )

    fun toggleTaskCompleted(task: StudyTaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(isCompleted = !task.isCompleted)
            )
        }
    }

    fun updateTaskDetails(
        task: StudyTaskEntity,
        title: String,
        priority: TaskPriority
    ) {
        val trimmedTitle = title.trim()

        if (trimmedTitle.isEmpty()) {
            return
        }

        viewModelScope.launch {
            taskRepository.updateTask(
                task.copy(
                    title = trimmedTitle,
                    priority = priority
                )
            )
        }
    }
}
