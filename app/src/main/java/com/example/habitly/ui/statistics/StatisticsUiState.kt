package com.example.habitly.ui.statistics

data class StatisticsUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val openTasks: Int = 0,
    val totalSessions: Int = 0,
    val totalFocusMinutes: Int = 0
)
