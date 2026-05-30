package com.example.habitly.ui.tasks

import com.example.habitly.data.local.entity.StudyTaskEntity

data class TasksUiState(
    val tasks: List<StudyTaskEntity> = emptyList(),
    val newTaskTitle: String = ""
)
