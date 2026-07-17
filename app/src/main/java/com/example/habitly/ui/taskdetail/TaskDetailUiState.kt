package com.example.habitly.ui.taskdetail

import com.example.habitly.data.local.entity.StudyTaskEntity

data class TaskDetailUiState(
    val task: StudyTaskEntity? = null,
    val sessions: List<TaskSessionSummary> = emptyList(),
    val totalFocusMinutes: Int = 0,
    val sessionCount: Int = 0
)

data class TaskSessionSummary(
    val id: Long,
    val durationMinutes: Int,
    val completedAt: Long
)
