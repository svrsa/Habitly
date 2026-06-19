package com.example.habitly.ui.statistics

data class StatisticsUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val openTasks: Int = 0,
    val totalSessions: Int = 0,
    val totalFocusMinutes: Int = 0,
    val todayFocusMinutes: Int = 0,
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val dailyFocusStats: List<DailyFocusStat> = emptyList(),
    val studyHeatmap: List<StudyHeatmapDay> = emptyList(),
    val recentSessions: List<RecentFocusSession> = emptyList()
)

data class DailyFocusStat(
    val label: String,
    val focusMinutes: Int
)

data class RecentFocusSession(
    val id: Long,
    val durationMinutes: Int,
    val completedLabel: String
)
